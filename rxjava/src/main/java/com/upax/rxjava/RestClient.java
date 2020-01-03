package com.upax.rxjava;

import retrofit.RestAdapter;

class RestClient {
    private static HNService hnService;

     static HNService getHNService() {
        if (RestClient.hnService == null) {
            RestAdapter retrofit = new RestAdapter.Builder()
                    .setEndpoint("https://hacker-news.firebaseio.com/v0")
                    .build();

            RestClient.hnService = retrofit.create(HNService.class);
        }

        return RestClient.hnService;
    }

}
