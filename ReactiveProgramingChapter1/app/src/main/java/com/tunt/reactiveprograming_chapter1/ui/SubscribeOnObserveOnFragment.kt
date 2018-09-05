package com.tunt.reactiveprograming_chapter1.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.tunt.reactiveprograming_chapter1.R
import com.tunt.reactiveprograming_chapter1.SpinnerBaseAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function5
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_subscribeon_observeon.*
import java.util.concurrent.TimeUnit

/**
 * Created by TuNT on 9/4/18.
 * tunt.program.04098@gmail.com
 */
class SubscribeOnObserveOnFragment : Fragment() {

    companion object {
        fun newInstance(): SubscribeOnObserveOnFragment {
            return SubscribeOnObserveOnFragment()
        }

        val samples = arrayListOf(
                Pair("Sample 1", "3 Observables running at the same time and on the same thread"),
                Pair("Sample 2", "3 Observables running sequentially and on the same thread"),
                Pair("Sample 3", "")
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_subscribeon_observeon, container, false)
    }

    private var timeInMillis = System.currentTimeMillis()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = SpinnerBaseAdapter(context, samples)
        spnSamples.adapter = adapter
        spnSamples.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, itemId: Long) {
                tvDescription.text = adapter.getItem(position).second
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        btnStart.setOnClickListener {
            timeInMillis = System.currentTimeMillis()
            val result = StringBuilder()
            when (spnSamples.selectedItemPosition) {
                0 -> sample1(result)
                1 -> sample2(result)
                else -> sample3(result)
            }
        }
    }

    private fun sample1(result: StringBuilder) {
        Observable.combineLatest(startObservable(result),
                firstObservable(result),
                secondObservable(result),
                thirdObservable(result),
                endObservable(result),
                Function5 { resultStart: StringBuilder,
                            result1: StringBuilder,
                            result2: StringBuilder,
                            result3: StringBuilder,
                            resultEnd: StringBuilder ->
                    resultEnd
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result -> tvResult.text = result.toString() }
    }

    private fun sample2(result: StringBuilder) {
        startObservable(result)
                .flatMap { result -> firstObservable(result) }
                .flatMap { result -> secondObservable(result) }
                .flatMap { result -> thirdObservable(result) }
                .flatMap { result -> endObservable(result) }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result -> tvResult.text = result.toString() }
    }

    private fun sample3(result: StringBuilder) {

    }

    private fun startObservable(result: StringBuilder): Observable<StringBuilder> {
        return Observable.create<StringBuilder> {
            val threadName = Thread.currentThread().name
            val time = (System.currentTimeMillis() - timeInMillis).toString() + "ms"
            result.append("Start: ($time)\n - Thread $threadName\n")
            result.append("\n====================================\n")
            it.onNext(result)
            it.onComplete()
        }
    }

    private fun firstObservable(result: StringBuilder): Observable<StringBuilder> {
        return Observable.create<StringBuilder> {
            Thread.sleep(100)
            val threadName = Thread.currentThread().name
            val time = (System.currentTimeMillis() - timeInMillis).toString() + "ms"
            result.append("\nfirstObservable: ($time)\n - Thread $threadName\n")
            it.onNext(result)
            it.onComplete()
        }
    }

    private fun secondObservable(result: StringBuilder): Observable<StringBuilder> {
        return Observable.create<StringBuilder> {
            Thread.sleep(300)
            val threadName = Thread.currentThread().name
            val time = (System.currentTimeMillis() - timeInMillis).toString() + "ms"
            result.append("\nsecondObservable: ($time)\n - Thread $threadName\n")
            it.onNext(result)
            it.onComplete()
        }
    }

    private fun thirdObservable(result: StringBuilder): Observable<StringBuilder> {
        return Observable.create<StringBuilder> {
            Thread.sleep(200)
            val threadName = Thread.currentThread().name
            val time = (System.currentTimeMillis() - timeInMillis).toString() + "ms"
            result.append("\nthirdObservable: ($time)\n - Thread $threadName\n")
            it.onNext(result)
            it.onComplete()
        }
    }

    private fun endObservable(result: StringBuilder): Observable<StringBuilder> {
        return Observable.create<StringBuilder> {
            val threadName = Thread.currentThread().name
            val time = (System.currentTimeMillis() - timeInMillis).toString() + "ms"
            result.append("\n====================================\n")
            result.append("\nEnd: ($time)\n - Thread $threadName\n")
            it.onNext(result)
            it.onComplete()
        }
    }
}