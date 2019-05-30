package com.rohith.urbandictionary.network;


import com.rohith.urbandictionary.dto.DefinitionList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface UrbanDictionaryApi {
//    01c19a11ecmsh392aec6735eedefp149f14jsnfbe21cd716b8
//    9eab91fd01msh5a5fff38abd4056p118939jsnf3b5725fe4e7

    @Headers({
            "Accept: application/json",
            "X-RapidAPI-Host: mashape-community-urban-dictionary.p.rapidapi.com",
            "X-RapidAPI-Key: 01c19a11ecmsh392aec6735eedefp149f14jsnfbe21cd716b8"
    })
    @GET("/define")
    Call<DefinitionList> getDefinitions(@Query("term") String term);

}
