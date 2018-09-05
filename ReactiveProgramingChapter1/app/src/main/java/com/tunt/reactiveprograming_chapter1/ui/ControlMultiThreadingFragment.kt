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
import io.reactivex.functions.Function3
import io.reactivex.functions.Function5
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_subscribeon_observeon.*
import java.util.concurrent.TimeUnit

/**
 * Created by TuNT on 9/4/18.
 * tunt.program.04098@gmail.com
 */
class ControlMultiThreadingFragment : Fragment() {

    companion object {
        fun newInstance(): ControlMultiThreadingFragment {
            return ControlMultiThreadingFragment()
        }

        val samples = arrayListOf(
                Pair("Sample 1", "• 3 Observables running at the SAME TIME and on DIFFERENT thread\n" +
                        "• start->combineLatest(first-second-third)->end"),
                Pair("Sample 2.1", "• 3 Observables running SEQUENTIALLY and on the SAME thread - 1\n" +
                        "• start->combineLatest(first-second-third)->end"),
                Pair("Sample 2.2", "• 3 Observables running SEQUENTIALLY and on the SAME thread - 2\n" +
                        "• start->first->second->third->end")
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
                0 -> sample1(result)
                1 -> sample21(result)
                2 -> sample22(result)
                else -> sample3(result)
            }
        }
    }

    private fun sample1(result: StringBuilder) {
        startObservable(result)
                .flatMap { _ ->
                    Observable.combineLatest(
                            firstObservable(result).subscribeOn(Schedulers.newThread()),
                            secondObservable(result).subscribeOn(Schedulers.newThread()),
                            thirdObservable(result).subscribeOn(Schedulers.newThread()),
                            Function3 { _: StringBuilder, _: StringBuilder, result: StringBuilder -> result })
                }
                .flatMap { result -> endObservable(result) }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result -> tvResult.text = result.toString() }
    }

    private fun sample21(result: StringBuilder) {
        startObservable(result)
                .flatMap { _ ->
                    Observable.combineLatest(
                            firstObservable(result),
                            secondObservable(result),
                            thirdObservable(result),
                            Function3 { _: StringBuilder, _: StringBuilder, result: StringBuilder -> result })
                }
                .flatMap { result -> endObservable(result) }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result -> tvResult.text = result.toString() }
    }

    private fun sample22(result: StringBuilder) {
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
            Log.e(AppConstants.TAG,"Start: ($time)\n - Thread $threadName\n")
            Log.e(AppConstants.TAG,"\n====================================\n")
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
            Log.e(AppConstants.TAG,"\nfirstObservable: ($time)\n - Thread $threadName\n")
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
            Log.e(AppConstants.TAG,"\nsecondObservable: ($time)\n - Thread $threadName\n")
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
            Log.e(AppConstants.TAG,"\nthirdObservable: ($time)\n - Thread $threadName\n")
            result.append("\nthirdObservable: ($time)\n - Thread $threadName\n")
            it.onNext(result)
            it.onComplete()
        }
    }

    private fun endObservable(result: StringBuilder): Observable<StringBuilder> {
        return Observable.create<StringBuilder> {
            val threadName = Thread.currentThread().name
            val time = (System.currentTimeMillis() - timeInMillis).toString() + "ms"
            Log.e(AppConstants.TAG,"\n====================================\n")
            Log.e(AppConstants.TAG,"\nEnd: ($time)\n - Thread $threadName\n")
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