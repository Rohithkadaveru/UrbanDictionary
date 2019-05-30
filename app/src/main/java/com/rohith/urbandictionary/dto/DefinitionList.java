package com.rohith.urbandictionary.dto;

import java.util.List;
import com.google.gson.annotations.SerializedName;


public class DefinitionList {

	@SerializedName("list")
	private List<Definition> list;

	public void setList(List<Definition> list){
		this.list = list;
	}

	public List<Definition> getList(){
		return list;
	}

	@Override
 	public String toString(){
		return
			"Response{" +
			"list = '" + list + '\'' +
			"}";
		}
}
