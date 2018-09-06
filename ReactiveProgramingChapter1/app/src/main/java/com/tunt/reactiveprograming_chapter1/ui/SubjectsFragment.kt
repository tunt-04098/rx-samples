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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.subjects.AsyncSubject
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import kotlinx.android.synthetic.main.fragment_subjects.*

/**
 * Created by TuNT on 9/4/18.
 * tunt.program.04098@gmail.com
 */
class SubjectsFragment : Fragment() {

    companion object {
        fun newInstance(): SubjectsFragment {
            return SubjectsFragment()
        }

        val samples = arrayListOf(
                Pair("AsyncSubject", "• "),
                Pair("BehaviorSubject", "• "),
                Pair("PublishSubject", "• "),
                Pair("ReplaySubject", "• ")
        )

        private var nextEmit = 4
    }

    private lateinit var asyncSubject: AsyncSubject<Int>

    private lateinit var behaviorSubject: BehaviorSubject<Int>

    private lateinit var publishSubject: PublishSubject<Int>

    private lateinit var replaySubject: ReplaySubject<Int>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_subjects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initSpinner()

        btnStart.setOnClickListener {
            tvSubscriber1.text = ""
            tvSubscriber2.text = ""
            nextEmit = 4
            when (spnSamples.selectedItemPosition) {
                0 -> asyncSubject()
                1 -> behaviorSubject()
                2 -> publishSubject()
                else -> replaySubject()
            }
        }
        btnEmit.setOnClickListener {
            when (spnSamples.selectedItemPosition) {
                0 -> asyncSubject.onNext(nextEmit)
                1 -> behaviorSubject.onNext(nextEmit)
                2 -> publishSubject.onNext(nextEmit)
                else -> replaySubject.onNext(nextEmit)
            }
            nextEmit++
        }
        btnError.setOnClickListener {
            asyncSubject.onError(Throwable(""))
        }
        btnComplete.setOnClickListener {
            asyncSubject.onComplete()
        }
    }

    private fun asyncSubject() {
        asyncSubject = AsyncSubject.create()
        asyncSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscribe1, Consumer { error -> Log.e(AppConstants.TAG, error.message )})
        asyncSubject.onNext(1)
        asyncSubject.onNext(2)
        asyncSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscribe2, Consumer { error -> Log.e(AppConstants.TAG, error.message )})
        asyncSubject.onNext(3)
    }

    private fun behaviorSubject() {
        behaviorSubject = BehaviorSubject.create()
        behaviorSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscribe1)
        behaviorSubject.onNext(1)
        behaviorSubject.onNext(2)
        behaviorSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscribe2)
        behaviorSubject.onNext(3)
    }

    private fun publishSubject() {
        publishSubject = PublishSubject.create()
        publishSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscribe1)
        publishSubject.onNext(1)
        publishSubject.onNext(2)
        publishSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscribe2)
        publishSubject.onNext(3)
    }

    private fun replaySubject() {
        replaySubject = ReplaySubject.create()
        replaySubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscribe1)
        replaySubject.onNext(1)
        replaySubject.onNext(2)
        replaySubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscribe2)
        replaySubject.onNext(3)
    }

    private val subscribe1 = Consumer<Int> { value ->
        tvSubscriber1.append("number: $value \n")
    }

    private val subscribe2 = Consumer<Int> { value ->
        tvSubscriber2.append("number: $value \n")
    }

    private fun initSpinner() {
        val adapter = SpinnerBaseAdapter(context, samples)
        spnSamples.adapter = adapter
        spnSamples.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, itemId: Long) {
                tvSubscriber1.text = ""
                tvSubscriber2.text = ""
                llAsyncSubject.visibility = if (position == 0) View.VISIBLE else View.GONE
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }
}