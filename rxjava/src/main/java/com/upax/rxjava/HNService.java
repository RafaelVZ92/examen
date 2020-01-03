package com.upax.rxjava;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

public interface HNService {
    @GET("/item/{itemId}.json")
    Observable<HNItem> getItem(@Path("itemId") String itemId);

    @GET("/newstories.json")
    Observable<NewStories> getNewStories();
}
