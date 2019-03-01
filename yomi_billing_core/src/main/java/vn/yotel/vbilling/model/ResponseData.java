package vn.yotel.vbilling.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;

public class ResponseData implements  Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String resultCode = "1";
	private String result;
	Object data;
	
	/**
	 * Class của TO
	 */
	@SuppressWarnings("rawtypes")
	Class cls;

//	Map<String,String> errors;
	
	@SuppressWarnings("rawtypes")
	public static ResponseData responseData(final Object content, Class cls) {
		ResponseData responseData = new ResponseData(cls);
		responseData.setData(content);
		responseData.build();
		return responseData;
	}

	public static ResponseData responseData(final String resultCode) {
		ResponseData responseData = new ResponseData(null);
		responseData.setResultCode(resultCode);
		responseData.build();
		return responseData;
	}
	
	public static ResponseData responseData(final String resultCode,final String result) {
		ResponseData responseData = new ResponseData(null);
		responseData.setResultCode(resultCode);
		responseData.setResult(result);
		responseData.build();
		return responseData;
	}
	
	public ResponseData() {
		this.resultCode = "1";
	}
	
	public ResponseData(String resultCode, String result) {
		this.resultCode = resultCode;
		this.result = result;
	}
	
	@SuppressWarnings("rawtypes")
	public ResponseData(Class cls) {
		this.cls = cls;
	}
	
	/**
	 * 
	 * @param page Page Items
	 * @param cls
	 */
	@SuppressWarnings({ "rawtypes" })
	public ResponseData(final Page page,Class cls) {
			this.build(page, cls);
	}
	/**
	 * 
	 * @param content Object item
	 * @param cls
	 */
	@SuppressWarnings("rawtypes")
	public ResponseData(final Object content, Class cls) {
		this.build(content, cls);
	}
	
	/**
	 * Page contain of items and page metadata
	 * @param page
	 * @param cls
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void build(final Page page,Class cls) {
//		if(page == null || cls ==null){
		if(page == null){
			return;
		}
//		this.pageInfo = new PageInfo();
//		this.pageInfo.setNumber(page.getNumber());
//		this.pageInfo.setNumberOfElements(page.getNumberOfElements());
//		this.pageInfo.setSize(page.getSize());
//		this.pageInfo.setTotalElements(page.getTotalElements());
//		this.pageInfo.setTotalPages(page.getTotalPages());
//		this.pageInfo.setFirst(page.isFirst());
//		this.pageInfo.setLast(page.isLast());
		
		List list = page.getContent();
		if(cls == null){
			this.data = list;
			return;
		}
		List dtoList = new ArrayList();
		Object dto = null; // cls.newInstance();
		for(Object e:list){
			try {
				dto = cls.newInstance();
				BeanUtils.copyProperties(e, dto);	
			} catch (Exception ex) {
				ex.printStackTrace();
			} 
			if(dto != null){
				dtoList.add(dto);
			}
		}
		this.data = dtoList;
	}
	
	/**
	 * Single Item
	 * @param content
	 * @param cls
	 */
	
	@SuppressWarnings("rawtypes")
	private void build(final Object content, Class cls) {
		if(content == null){
			return ;
		}
		if(cls == null){
			this.data = content;
			return;
		}
		Object dto = null;
		try {
			dto = cls.newInstance();
			BeanUtils.copyProperties(content, dto);	
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
		this.data = dto;
	}
	
	/**
	 * fix list items 
	 * @param items
	 * @param cls
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void build(final List items,Class cls) {
//		if(items == null || items.isEmpty() || cls ==null){
		if(items == null || items.isEmpty()){
			return;
		}
		if(cls == null){
			this.data = items;
			return;
		}
//		this.pageInfo = new PageInfo();
//		this.pageInfo.setNumber(0); // page number
//		this.pageInfo.setNumberOfElements(items.size());
//		this.pageInfo.setSize(items.size());
//		this.pageInfo.setTotalElements(items.size());
//		this.pageInfo.setTotalPages(1);
//		this.pageInfo.setFirst(true);
//		this.pageInfo.setLast(true);
//		
		List dtoList = new ArrayList();
		Object dto = null; // cls.newInstance();
		for(Object e:items){
			try {
				dto = cls.newInstance();
				BeanUtils.copyProperties(e, dto);	
			} catch (Exception ex) {
				ex.printStackTrace();
			} 
			if(dto != null){
				dtoList.add(dto);
			}
		}
		this.data = dtoList;
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public ResponseData build() {
		if (this.data instanceof Page) {
			Page page = (Page) this.data;
			this.build(page, this.cls);
		} else if (this.data instanceof List) {
			List items = (List) this.data;
			this.build(items, cls);
		} else {
			this.build(this.data, this.cls);
		}
		return this;
	}
	

	public Object getData() {
		return data;
	}

	public void setData(Object content) {
		this.data = content;
	}
	
	
//	public PageInfo getPageInfo() {
//		return pageInfo;
//	}

//	public Timestamp getTimestamp() {
//		return timestamp;
//	}
	
//	public Map<String, String> getErrors() {
//		return errors;
//	}
//
//	public void setErrors(Map<String, String> errors) {
//		this.errors = errors;
//	}

	/**
	 * 
	 * Page metadata
	 *
	 */
	class PageInfo {
		// sort
		int numberOfElements;
		int totalPages;
		long totalElements;
		int size;
		int number;
		boolean first;
		boolean last;
		
		public int getNumberOfElements() {
			return numberOfElements;
		}
		public void setNumberOfElements(int numberOfElements) {
			this.numberOfElements = numberOfElements;
		}
		public int getTotalPages() {
			return totalPages;
		}
		public void setTotalPages(int totalPages) {
			this.totalPages = totalPages;
		}
		public long getTotalElements() {
			return totalElements;
		}
		public void setTotalElements(long totalElements) {
			this.totalElements = totalElements;
		}
		public int getSize() {
			return size;
		}
		public void setSize(int size) {
			this.size = size;
		}
		public int getNumber() {
			return number;
		}
		public void setNumber(int number) {
			this.number = number;
		}
		public boolean isFirst() {
			return first;
		}
		public void setFirst(boolean first) {
			this.first = first;
		}
		public boolean isLast() {
			return last;
		}
		public void setLast(boolean last) {
			this.last = last;
		}
		@Override
		public String toString() {
			return "PageInfo [numberOfElements=" + numberOfElements
					+ ", totalPages=" + totalPages + ", totalElements="
					+ totalElements + ", size=" + size + ", number=" + number
					+ ", first=" + first + ", last=" + last + "]";
		}
	}


	/**
	 * @return the resultCode
	 */
	public String getResultCode() {
		return resultCode;
	}

	/**
	 * @param resultCode the resultCode to set
	 */
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}
	
}
