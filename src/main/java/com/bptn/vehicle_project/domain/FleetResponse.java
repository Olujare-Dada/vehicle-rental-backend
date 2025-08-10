package com.bptn.vehicle_project.domain;

import java.util.List;

public class FleetResponse {
    private boolean success;
    private String message;
    private FleetData data;

    public FleetResponse() {
    }

    public FleetResponse(boolean success, String message, FleetData data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FleetData getData() {
        return data;
    }

    public void setData(FleetData data) {
        this.data = data;
    }

    public static class FleetData {
        private List<VehicleInfo> vehicles;
        private PaginationInfo pagination;

        public FleetData() {
        }

        public FleetData(List<VehicleInfo> vehicles, PaginationInfo pagination) {
            this.vehicles = vehicles;
            this.pagination = pagination;
        }

        public List<VehicleInfo> getVehicles() {
            return vehicles;
        }

        public void setVehicles(List<VehicleInfo> vehicles) {
            this.vehicles = vehicles;
        }

        public PaginationInfo getPagination() {
            return pagination;
        }

        public void setPagination(PaginationInfo pagination) {
            this.pagination = pagination;
        }
    }

    public static class VehicleInfo {
        private Integer id;
        private String name;
        private String type;
        private String price;
        private String description;
        private List<String> features;
        private String image;
        private String icon;
        private String status;

        public VehicleInfo() {
        }

        public VehicleInfo(Integer id, String name, String type, String price, String description, 
                          List<String> features, String image, String icon, String status) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.price = price;
            this.description = description;
            this.features = features;
            this.image = image;
            this.icon = icon;
            this.status = status;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<String> getFeatures() {
            return features;
        }

        public void setFeatures(List<String> features) {
            this.features = features;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class PaginationInfo {
        private int currentPage;
        private int totalPages;
        private int totalItems;
        private int itemsPerPage;
        private boolean hasNext;
        private boolean hasPrevious;

        public PaginationInfo() {
        }

        public PaginationInfo(int currentPage, int totalPages, int totalItems, int itemsPerPage, 
                             boolean hasNext, boolean hasPrevious) {
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.totalItems = totalItems;
            this.itemsPerPage = itemsPerPage;
            this.hasNext = hasNext;
            this.hasPrevious = hasPrevious;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(int totalItems) {
            this.totalItems = totalItems;
        }

        public int getItemsPerPage() {
            return itemsPerPage;
        }

        public void setItemsPerPage(int itemsPerPage) {
            this.itemsPerPage = itemsPerPage;
        }

        public boolean isHasNext() {
            return hasNext;
        }

        public void setHasNext(boolean hasNext) {
            this.hasNext = hasNext;
        }

        public boolean isHasPrevious() {
            return hasPrevious;
        }

        public void setHasPrevious(boolean hasPrevious) {
            this.hasPrevious = hasPrevious;
        }
    }
} 