package handlers

import (
	"net/http"

	"smarthome/services"

	"github.com/gin-gonic/gin"
)

// DeviceHandler handles device-related requests (API Gateway)
type DeviceHandler struct {
	DeviceManagementService *services.DeviceManagementService
}

// NewDeviceHandler creates a new DeviceHandler
func NewDeviceHandler(deviceManagementService *services.DeviceManagementService) *DeviceHandler {
	return &DeviceHandler{
		DeviceManagementService: deviceManagementService,
	}
}

// RegisterRoutes registers the device routes
func (h *DeviceHandler) RegisterRoutes(router *gin.RouterGroup) {
	devices := router.Group("/devices")
	{
		devices.POST("", h.CreateDevice)
		devices.GET("", h.ListDevices)
		devices.GET("/:id", h.GetDevice)
	}
}

// CreateDevice handles POST /api/v1/devices - proxies to device-management service
func (h *DeviceHandler) CreateDevice(c *gin.Context) {
	var req services.DeviceCreateRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	// Proxy the request to device-management service
	device, err := h.DeviceManagementService.CreateDevice(c.Request.Context(), req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, device)
}

// ListDevices handles GET /api/v1/devices - proxies to device-management service
func (h *DeviceHandler) ListDevices(c *gin.Context) {
	homeID := c.Query("homeId")
	typeCode := c.Query("typeCode")
	location := c.Query("location")

	// Proxy the request to device-management service
	devices, err := h.DeviceManagementService.ListDevices(c.Request.Context(), homeID, typeCode, location)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, devices)
}

// GetDevice handles GET /api/v1/devices/:id - proxies to device-management service
func (h *DeviceHandler) GetDevice(c *gin.Context) {
	deviceID := c.Param("id")
	if deviceID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Device ID is required"})
		return
	}

	// Proxy the request to device-management service
	device, err := h.DeviceManagementService.GetDevice(c.Request.Context(), deviceID)
	if err != nil {
		if err.Error() == "device not found" {
			c.JSON(http.StatusNotFound, gin.H{"error": "Device not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, device)
}
