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
import kotlinx.android.synthetic.main.fragment_samples.*

/**
 * Created by TuNT on 9/4/18.
 * tunt.program.04098@gmail.com
 */
class SchedulersFragment : Fragment() {

    companion object {
        fun newInstance(): SchedulersFragment {
            return SchedulersFragment()
        }

        val samples = arrayListOf(
                Pair("Schedulers.io()", "• scheduler.io() implementation backed to thread-pool that will grow as needed. So, we don’t know which thread will be used whenever we call Schedulers.io()"),
                Pair("Schedulers.trampoline()", "• All jobs that subscribes on trampoline() will be queued and excuted one by one"),
                Pair("Schedulers.newThread()", "• Using flatMap and subscribeOn we can execute an item per thread")
        )

        private var timeInMillis = System.currentTimeMillis()
        private var result = StringBuilder()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_samples, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initSpinner()

        btnStart.setOnClickListener {
            timeInMillis = System.currentTimeMillis()
            result = StringBuilder()
            tvResult.text = ""
            when (spnSamples.selectedItemPosition) {
                0 -> io()
                1 -> trampoline()
                else -> itemPerThread()
            }
        }
    }

    private fun io() {
        Observable.just(2, 4, 6, 8, 10)
                .map { number ->
                    val threadName = Thread.currentThread().name
                    val time = (System.currentTimeMillis() - timeInMillis).toString() + "ms"
                    val log = "\nnumber: $number ($time)\n - Thread $threadName\n"
                    Log.e(AppConstants.TAG, log)
                    result.append(log)
                    log
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { log -> tvResult.append(log) }
        Observable.just(1, 3, 5, 7, 9)
                .map { number ->
                    val threadName = Thread.currentThread().name
                    val time = (System.currentTimeMillis() - timeInMillis).toString() + "ms"
                    val log = "\nnumber: $number ($time)\n - Thread $threadName\n"
                    Log.e(AppConstants.TAG, log)
                    result.append(log)
                    log
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { log -> tvResult.append(log) }
    }

    private fun trampoline() {
        Observable.just(2, 4, 6, 8, 10)
                .map { number ->
                    val threadName = Thread.currentThread().name
                    val time = (System.currentTimeMillis() - timeInMillis).toString() + "ms"
                    val log = "\nnumber: $number ($time)\n - Thread $threadName\n"
                    Log.e(AppConstants.TAG, log)
                    result.append(log)
                    log
                }
                .subscribeOn(Schedulers.trampoline())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { log -> tvResult.append(log) }
        Observable.just(1, 3, 5, 7, 9)
                .map { number ->
                    val threadName = Thread.currentThread().name
                    val time = (System.currentTimeMillis() - timeInMillis).toString() + "ms"
                    val log = "\nnumber: $number ($time)\n - Thread $threadName\n"
                    Log.e(AppConstants.TAG, log)
                    result.append(log)
                    log
                }
                .subscribeOn(Schedulers.trampoline())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { log -> tvResult.append(log) }
    }

    private fun itemPerThread() {
        Observable.just(1, 2, 3, 4, 5)
                .flatMap { number ->
                    Observable.just(number)
                            .map { number ->
                                val threadName = Thread.currentThread().name
                                val time = (System.currentTimeMillis() - timeInMillis).toString() + "ms"
                                val log = "\nnumber: $number ($time)\n - Thread $threadName\n"
                                Log.e(AppConstants.TAG, log)
                                result.append(log)
                                log
                            }
                            .subscribeOn(Schedulers.newThread())
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { log -> tvResult.append(log) }
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