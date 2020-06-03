package com.airmon.dto;


import java.util.ArrayList;
import java.util.List;

public class ResponseDto {
	private int code;
	private String message;
	private boolean status;
	private List<?> data;
	
	
	public ResponseDto() {
		super();
	}
	
	public ResponseDto(int code, String message, boolean status, Object data) {
		super();
		this.code = code;
		this.message = message;
		this.status = status;
		this.setData(data);
	}
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public List<?> getData() {
		return data;
	}


	@Override
	public String toString() {
		return "ResponseDto [code=" + code + ", message=" + message + ", status=" + status + ", data=" + data + "]";
	}

	public void setData(Object data) {
		List<Object> list = new ArrayList<Object>();
		if(data != null) {
			if(!(data instanceof List)) {
				list.add(data);
				this.data = list;
			}
			else {
				this.data = (List<?>) data;
			}
		}
	}
}

