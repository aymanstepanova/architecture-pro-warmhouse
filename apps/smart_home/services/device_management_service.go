package services

import (
	"bytes"
	"context"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"time"
)

// DeviceManagementService handles communication with device-management service
type DeviceManagementService struct {
	baseURL    string
	httpClient *http.Client
}

// DeviceCreateRequest represents the request to create a device
type DeviceCreateRequest struct {
	DeviceKey    string                 `json:"deviceKey" binding:"required"`
	TypeCode     string                 `json:"typeCode" binding:"required"`
	Model        string                 `json:"model,omitempty"`
	Location     string                 `json:"location,omitempty"`
	Status       string                 `json:"status,omitempty"`
	Metadata     map[string]interface{} `json:"metadata,omitempty"`
	HomeID       string                 `json:"homeId,omitempty"`
	OwnerAccount string                 `json:"ownerAccount,omitempty"`
}

// DeviceResponse represents the response from device-management service
type DeviceResponse struct {
	ID           string                 `json:"id"`
	DeviceKey    string                 `json:"deviceKey"`
	TypeCode     string                 `json:"typeCode"`
	Model        string                 `json:"model"`
	Location     string                 `json:"location"`
	Status       string                 `json:"status"`
	Metadata     map[string]interface{} `json:"metadata"`
	HomeID       string                 `json:"homeId"`
	OwnerAccount string                 `json:"ownerAccount"`
}

// DeviceListResponse represents the response for listing devices
type DeviceListResponse []DeviceResponse

// NewDeviceManagementService creates a new DeviceManagementService
func NewDeviceManagementService(baseURL string) *DeviceManagementService {
	return &DeviceManagementService{
		baseURL: baseURL,
		httpClient: &http.Client{
			Timeout: 30 * time.Second,
		},
	}
}

// CreateDevice creates a new device in device-management service
func (s *DeviceManagementService) CreateDevice(ctx context.Context, req DeviceCreateRequest) (*DeviceResponse, error) {
	jsonData, err := json.Marshal(req)
	if err != nil {
		return nil, fmt.Errorf("failed to marshal request: %w", err)
	}

	url := fmt.Sprintf("%s/devices", s.baseURL)
	httpReq, err := http.NewRequestWithContext(ctx, "POST", url, bytes.NewBuffer(jsonData))
	if err != nil {
		return nil, fmt.Errorf("failed to create request: %w", err)
	}

	httpReq.Header.Set("Content-Type", "application/json")

	resp, err := s.httpClient.Do(httpReq)
	if err != nil {
		return nil, fmt.Errorf("failed to send request: %w", err)
	}
	defer resp.Body.Close()

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return nil, fmt.Errorf("failed to read response body: %w", err)
	}

	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("device-management service returned status %d: %s", resp.StatusCode, string(body))
	}

	var deviceResp DeviceResponse
	if err := json.Unmarshal(body, &deviceResp); err != nil {
		return nil, fmt.Errorf("failed to unmarshal response: %w", err)
	}

	return &deviceResp, nil
}

// GetDevice retrieves a device by ID from device-management service
func (s *DeviceManagementService) GetDevice(ctx context.Context, deviceID string) (*DeviceResponse, error) {
	url := fmt.Sprintf("%s/devices/%s", s.baseURL, deviceID)
	httpReq, err := http.NewRequestWithContext(ctx, "GET", url, nil)
	if err != nil {
		return nil, fmt.Errorf("failed to create request: %w", err)
	}

	resp, err := s.httpClient.Do(httpReq)
	if err != nil {
		return nil, fmt.Errorf("failed to send request: %w", err)
	}
	defer resp.Body.Close()

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return nil, fmt.Errorf("failed to read response body: %w", err)
	}

	if resp.StatusCode == http.StatusNotFound {
		return nil, fmt.Errorf("device not found")
	}

	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("device-management service returned status %d: %s", resp.StatusCode, string(body))
	}

	var deviceResp DeviceResponse
	if err := json.Unmarshal(body, &deviceResp); err != nil {
		return nil, fmt.Errorf("failed to unmarshal response: %w", err)
	}

	return &deviceResp, nil
}

// ListDevices retrieves a list of devices from device-management service
func (s *DeviceManagementService) ListDevices(ctx context.Context, homeID, typeCode, location string) (DeviceListResponse, error) {
	url := fmt.Sprintf("%s/devices", s.baseURL)
	httpReq, err := http.NewRequestWithContext(ctx, "GET", url, nil)
	if err != nil {
		return nil, fmt.Errorf("failed to create request: %w", err)
	}

	// Add query parameters
	q := httpReq.URL.Query()
	if homeID != "" {
		q.Add("homeId", homeID)
	}
	if typeCode != "" {
		q.Add("typeCode", typeCode)
	}
	if location != "" {
		q.Add("location", location)
	}
	httpReq.URL.RawQuery = q.Encode()

	resp, err := s.httpClient.Do(httpReq)
	if err != nil {
		return nil, fmt.Errorf("failed to send request: %w", err)
	}
	defer resp.Body.Close()

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return nil, fmt.Errorf("failed to read response body: %w", err)
	}

	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("device-management service returned status %d: %s", resp.StatusCode, string(body))
	}

	var devices DeviceListResponse
	if err := json.Unmarshal(body, &devices); err != nil {
		return nil, fmt.Errorf("failed to unmarshal response: %w", err)
	}

	return devices, nil
}
