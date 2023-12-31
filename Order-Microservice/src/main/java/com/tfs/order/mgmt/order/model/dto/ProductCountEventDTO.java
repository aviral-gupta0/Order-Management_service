package com.tfs.order.mgmt.order.model.dto;

import com.tfs.order.mgmt.order.enums.ActionType;

public class ProductCountEventDTO {
	
	public String orderId;
	public String productId;
	public int productCount;
	public ActionType actionType;
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	public int getProductCount() {
		return productCount;
	}
	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}
	public ActionType getActionType() {
		return actionType;
	}
	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}
	
		
}
