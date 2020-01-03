package com.upax.rxjava;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.ListFragment;

import com.google.common.collect.Lists;
import com.jakewharton.rxbinding.widget.RxAdapterView;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RxActivity extends AppCompatActivity {
    private static String TAG = "RX_JAVA_INTRO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx);
        ListFragment listFragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.listFragment);
        //Obtiene la instacia de cliente retrofit y se la asigna a la interfaz
        HNService hnService = RestClient.getHNService();

        //creamos el array adapter para el list fragment
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        //le pasamos el adapter al listfragment
        listFragment.setListAdapter(adapter);

        //la interfaz es un objeto observable
        hnService.getNewStories()
                //Significa que se ejecutara en un hilo totalmente diferente.
                .subscribeOn(Schedulers.io())
                //Transforma los elemetos emitidos por el observable a una funcion.
                .map(newStories -> getItemObservables(hnService, newStories))
                .flatMap(Observable::merge)
                .doOnNext(hnItem1 -> Log.d(TAG, "item received " + hnItem1.id))
                //para tomar 10 elementos
                .take(10)
                //pinta nuevamente en el hilo pricipal
                .observeOn(AndroidSchedulers.mainThread())
                //nos suscribimos nuevamente y vamos agregando a la lista el objeto en este caso el titulo mas el adaptador
                .subscribe((hnItem) -> addItemToList(hnItem, adapter),
                        Throwable::printStackTrace);

        ListView listView = listFragment.getListView();
        //clase externa para el bindeo de datos
        RxAdapterView.itemClicks(listView)
                .subscribe(position -> Log.d(TAG, "Item at position " + position + " pressed"));
    }

    private void addItemToList(HNItem hnItem, ArrayAdapter<String> adapter) {
        adapter.add(hnItem.title);
    }

    private List<Observable<HNItem>> getItemObservables(HNService hnService, NewStories newStories) {
        return Lists.transform(newStories, hnService::getItem);
    }

}
