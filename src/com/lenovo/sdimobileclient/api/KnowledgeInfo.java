package com.lenovo.sdimobileclient.api;

/**
 * 技术通报和其他文章
 */
public class KnowledgeInfo extends AbsApiData{
	/**
	 * 文章ID
	 */
	public String KnowledgeID;
	/**
	 * 文章标题
	 */
	public String KnowledgeTitle;
	/**
	 * 文章摘要
	 */
	public String KnowledgeSummary;
	public String KnowledgeType;
	/**
	 * 文章作者
	 */
	public String KnowledgeAuthor="";
	/**
	 * 文章发布时间
	 */
	public String KnowledgeDate;
	/**
	 * 文章路径
	 */
	public String KnowledgeURL;

}

