package com.tunt.reactiveprograming_chapter1.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.tunt.reactiveprograming_chapter1.AppConstants
import com.tunt.reactiveprograming_chapter1.R
import com.tunt.reactiveprograming_chapter1.SpinnerBaseAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.TestSubscriber
import kotlinx.android.synthetic.main.fragment_subscribeon_observeon.*
import java.util.concurrent.TimeUnit

/**
 * Created by TuNT on 9/4/18.
 * tunt.program.04098@gmail.com
 */
class ObservableFragment : Fragment() {

    companion object {
        fun newInstance(): ObservableFragment {
            return ObservableFragment()
        }

        val samples = arrayListOf(
                Pair("Sample 1", "• Observable.just(1)"),
                Pair("Sample 1.1", "• Observable.just(arrayOf(1, 2, 3)\n" +
                        "• convert an object or a set of objects into an Observable that emits that or those objects"),
                Pair("Sample 2", "• Observable.range(1, 10)\n" +
                        "• create an Observable that emits a range of sequential integers"),
                Pair("Sample 3", "• Observable.interval(500, TimeUnit.MILLISECONDS).repeat(5)\n" +
                        "• emits 5 times by a particular time interval")
        )

        private var timeInMillis = System.currentTimeMillis()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_samples, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initSpinner()

        btnStart.setOnClickListener {
            timeInMillis = System.currentTimeMillis()
            val result = StringBuilder()
            when (spnSamples.selectedItemPosition) {
                0 -> sample1()
                1 -> sample11()
                2 -> sample2()
                else -> sample4()
            }
        }
    }

    private fun sample1() {
        Observable.just(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result -> tvResult.append("$result\n") }
    }

    private fun sample11() {
        Observable.just(arrayOf(1, 2, 3))
                .map { result -> result.joinToString() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result -> tvResult.append("$result\n") }
    }

    private fun sample2() {
        val subscription = Observable.interval(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .takeUntil { time -> time > 2500 }
                .map { result ->
                    {
                        Log.e(AppConstants.TAG, "$result (ms)")
                        "$result (ms)"
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
//                .subscribe { result -> tvResult.append("$result\n") }
    }

    private fun sample4() {

    }

    private fun startObservable(result: StringBuilder): Observable<StringBuilder> {
        return Observable.create<StringBuilder> {
            val threadName = Thread.currentThread().name
            val time = (System.currentTimeMillis() - timeInMillis).toString() + "ms"
            Log.e(AppConstants.TAG, "Start: ($time)\n - Thread $threadName\n")
            Log.e(AppConstants.TAG, "\n====================================\n")
            result.append("Start: ($time)\n - Thread $threadName\n")
            result.append("\n====================================\n")
            it.onNext(result)
            it.onComplete()
        }
    }

    private fun firstObservable(result: StringBuilder): Observable<Int> {
        return Observable.just(1);

//        return Observable.create<StringBuilder> {
//            Thread.sleep(100)
//            val threadName = Thread.currentThread().name
//            val time = (System.currentTimeMillis() - timeInMillis).toString() + "ms"
//            Log.e(AppConstants.TAG,"\nfirstObservable: ($time)\n - Thread $threadName\n")
//            result.append("\nfirstObservable: ($time)\n - Thread $threadName\n")
//            it.onNext(result)
//            it.onComplete()
//        }
    }

    private fun secondObservable(result: StringBuilder): Observable<StringBuilder> {
        return Observable.create<StringBuilder> {
            Thread.sleep(300)
            val threadName = Thread.currentThread().name
            val time = (System.currentTimeMillis() - timeInMillis).toString() + "ms"
            Log.e(AppConstants.TAG, "\nsecondObservable: ($time)\n - Thread $threadName\n")
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
            Log.e(AppConstants.TAG, "\nthirdObservable: ($time)\n - Thread $threadName\n")
            result.append("\nthirdObservable: ($time)\n - Thread $threadName\n")
            it.onNext(result)
            it.onComplete()
        }
    }

    private fun endObservable(result: StringBuilder): Observable<StringBuilder> {
        return Observable.create<StringBuilder> {
            val threadName = Thread.currentThread().name
            val time = (System.currentTimeMillis() - timeInMillis).toString() + "ms"
            Log.e(AppConstants.TAG, "\n====================================\n")
            Log.e(AppConstants.TAG, "\nEnd: ($time)\n - Thread $threadName\n")
            result.append("\n====================================\n")
            result.append("\nEnd: ($time)\n - Thread $threadName\n")
            it.onNext(result)
            it.onComplete()
        }
    }

    private fun initSpinner() {
        val adapter = SpinnerBaseAdapter(context, samples)
        spnSamples.adapter = adapter
        spnSamples.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, itemId: Long) {
                tvDescription.text = adapter.getItem(position).second
                tvResult.text = ""
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }
}