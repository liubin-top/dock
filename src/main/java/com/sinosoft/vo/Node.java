package com.sinosoft.vo;

import java.io.Serializable;
import java.util.List;

public class Node implements Serializable{
	private String id;
	private String title;
	private String parentId;
	private String icon;
	private String selectedIcon;
	private String color;
	private String serverUrl;
	private String backColor;
	private Boolean isButton;
	private boolean disabled;
	private List<Node> nodes;
	private String templateType;//项目类型
	public Node(){
	}
	public Node(String id, String title){
		this.id = id;
		this.title=title;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public List<Node> getNodes() {
		return nodes;
	}
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getSelectedIcon() {
		return selectedIcon;
	}
	public void setSelectedIcon(String selectedIcon) {
		this.selectedIcon = selectedIcon;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getBackColor() {
		return backColor;
	}
	public void setBackColor(String backColor) {
		this.backColor = backColor;
	}
	public String getServerUrl() {
		return serverUrl;
	}
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	public Boolean getIsButton() {
		return isButton;
	}
	public void setIsButton(Boolean isButton) {
		this.isButton = isButton;
	}

	public Boolean getButton() {
		return isButton;
	}

	public void setButton(Boolean button) {
		isButton = button;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getTemplateType() {
		return templateType;
	}

	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}
}
