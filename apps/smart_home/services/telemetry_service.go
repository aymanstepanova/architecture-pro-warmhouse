package services

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"log"
	"net"
	"net/http"
	"net/url"
	"time"
	"github.com/google/uuid"
)

// ========= DTO из Telemetry API =========

type MetricDTO struct {
	ExternalSensorId string                 `json:"externalSensorId"`
	SensorType       string                 `json:"sensorType"`
	Unit             string                 `json:"unit"`
	Location         string                 `json:"location"`
	MetricCode       string                 `json:"metricCode"`
	Value            float64                `json:"value"`
	ValueJson        map[string]any         `json:"valueJson"` // опционально
	MeasuredAt       time.Time              `json:"measuredAt"`
}

// ========= Публичный интерфейс сервиса =========

type TelemetryService interface {
	// Последнее значение по внешнему sensor_id (из монолита/реестра)
	GetLatestBySensorExternal(ctx context.Context, externalSensorID string) (*MetricDTO, error)

	// Последние значения по локации; metricCode можно не передавать (nil) — вернём все типы
	GetLatestByLocation(ctx context.Context, location string, metricCode *string) ([]MetricDTO, error)
}

// ========= Опции и конструктор =========

type TelemetryOption func(*telemetryService)

func WithHTTPClient(c *http.Client) TelemetryOption {
	return func(s *telemetryService) { s.http = c }
}

func WithLogger(l *log.Logger) TelemetryOption {
	return func(s *telemetryService) { s.logger = l }
}

type telemetryService struct {
	baseURL *url.URL
	http *http.Client
	logger  *log.Logger
}

func NewTelemetryService(base string, opts ...TelemetryOption) (TelemetryService, error) {
	if base == "" {
		return nil, errors.New("telemetry base URL is empty")
	}
	u, err := url.Parse(base)
	if err != nil {
		return nil, fmt.Errorf("invalid telemetry base URL: %w", err)
	}

	// Дефолтный http.Client с таймаутами
	defaultHTTP := &http.Client{
		Timeout: 2 * time.Second,
		Transport: &http.Transport{
			DialContext: (&net.Dialer{
				Timeout:   500 * time.Millisecond,
				KeepAlive: 30 * time.Second,
			}).DialContext,
			MaxIdleConns:        100,
			MaxIdleConnsPerHost: 10,
			IdleConnTimeout:     90 * time.Second,
		},
	}

	s := &telemetryService{
		baseURL: u,
		http:    defaultHTTP,
		logger:  log.Default(),
	}
	for _, o := range opts {
		o(s)
	}
	return s, nil
}

// ========= Реализация методов =========

func (s *telemetryService) GetLatestBySensorExternal(ctx context.Context, externalSensorID string) (*MetricDTO, error) {
	if externalSensorID == "" {
		return nil, errors.New("external sensor id is empty")
	}
	rel := &url.URL{Path: fmt.Sprintf("/metrics/%s", url.PathEscape(externalSensorID))}
	q := rel.Query()
	q.Set("useExternalId", "true")
	rel.RawQuery = q.Encode()

	var out MetricDTO
	if err := s.doGET(ctx, rel, &out); err != nil {
		return nil, err
	}
	return &out, nil
}

func (s *telemetryService) GetLatestByLocation(ctx context.Context, location string, metricCode *string) ([]MetricDTO, error) {
	if location == "" {
		return nil, errors.New("location is empty")
	}
	rel := &url.URL{Path: "/metrics"}
	q := rel.Query()
	q.Set("location", location)
	if metricCode != nil && *metricCode != "" {
		q.Set("metric_code", *metricCode)
	}
	rel.RawQuery = q.Encode()

	var out []MetricDTO
	if err := s.doGET(ctx, rel, &out); err != nil {
		return nil, err
	}
	return out, nil
}

// ========= Внутренности HTTP-вызовов =========

func (s *telemetryService) doGET(ctx context.Context, rel *url.URL, v any) error {
	u := s.baseURL.ResolveReference(rel)

	req, err := http.NewRequestWithContext(ctx, http.MethodGet, u.String(), nil)
	if err != nil {
		return fmt.Errorf("build request: %w", err)
	}

	// Корреляция — берём из контекста, если есть; иначе генерим
	cid := correlationIDFromCtx(ctx)
	if cid == "" {
		cid = uuid.NewString()
	}
	req.Header.Set("X-Correlation-Id", cid)
	req.Header.Set("Accept", "application/json")

	resp, err := s.http.Do(req)
	if err != nil {
		return fmt.Errorf("telemetry http error: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode == http.StatusNotFound {
		return fmt.Errorf("telemetry: not found (%s)", u.Path)
	}
	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		return fmt.Errorf("telemetry: bad status %d for %s", resp.StatusCode, u.String())
	}

	dec := json.NewDecoder(resp.Body)
	dec.DisallowUnknownFields() // чтобы ловить неожиданные поля
	if err := dec.Decode(v); err != nil {
		return fmt.Errorf("telemetry decode: %w", err)
	}
	return nil
}

// ========= Корреляция =========

// Введи этот ключ в своём проекте рядом с middleware трассировки
type ctxKey string

const correlationKey ctxKey = "correlation-id"

func correlationIDFromCtx(ctx context.Context) string {
	if ctx == nil {
		return ""
	}
	if v := ctx.Value(correlationKey); v != nil {
		if s, ok := v.(string); ok {
			return s
		}
	}
	return ""
}
