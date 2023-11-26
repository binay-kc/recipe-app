package com.binay.recipeapp.uis.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import com.binay.recipeapp.R
import com.binay.recipeapp.databinding.ActivityCookingTimerBinding

class CookingTimerActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityCookingTimerBinding
    private var timeInMinutes: Int? = null
    private var isAlreadyLessThanHour = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCookingTimerBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        if (intent?.extras?.containsKey("readyInMinutes") == true) {
            timeInMinutes = intent?.extras?.getInt("readyInMinutes")
        }

        initView()
    }

    private fun initView() {
        mBinding.toolbar.toolbarTitle.text = getString(R.string.label_timer)
        mBinding.backBtn.setOnClickListener { onBackPressedDispatcher.addCallback { finish() } }
        if (timeInMinutes != null) {
            val hours = timeInMinutes!! / 60
            if (hours <= 0) {
                isAlreadyLessThanHour = true
                mBinding.llHour.visibility = View.GONE
            }
            val progressMaxLimit = timeInMinutes!! * 60
            mBinding.cpvTimer.max = 100
            val timeInMilliSeconds = (timeInMinutes!! * 60 * 1000).toLong()
            var countDownInterval = 1
            object : CountDownTimer(timeInMilliSeconds, countDownInterval * 1000L) {
                override fun onTick(p0: Long) {
                    countDownInterval += 1
                    val totalSecondsRemaining = p0 / 1000
                    val totalMinutesRemaining = totalSecondsRemaining / 60
                    val secondsRemainingToDisplay =
                        totalSecondsRemaining - (totalMinutesRemaining * 60)
                    val progressPercentage = (100 * countDownInterval) / progressMaxLimit
                    mBinding.cpvTimer.progress = progressPercentage
                    Log.e("$countDownInterval Progress", " $progressPercentage%")
                    Log.e(
                        "Remaining ",
                        " Minutes: $totalMinutesRemaining Seconds: $totalSecondsRemaining"
                    )
                    if (!isAlreadyLessThanHour) {
                        val hoursRemaining = totalMinutesRemaining / 60
                        if (hoursRemaining <= 0) {
                            mBinding.tvHourTenth.text = getString(R.string.label_default_hour)
                            mBinding.tvHourZeroth.text = getString(R.string.label_default_hour)
                        } else {
                            val tenthHr = hoursRemaining / 10
                            val zerothHr = hoursRemaining % 10
                            mBinding.tvHourTenth.text = tenthHr.toString()
                            mBinding.tvHourZeroth.text = zerothHr.toString()
                        }
                    }

                    if (totalMinutesRemaining <= 0) {
                        mBinding.tvMinuteTenth.text = getString(R.string.label_default_hour)
                        mBinding.tvMinuteZeroth.text = getString(R.string.label_default_hour)
                    } else {
                        val tenthMin = totalMinutesRemaining / 10
                        val zerothMin = totalMinutesRemaining % 10
                        mBinding.tvMinuteTenth.text = tenthMin.toString()
                        mBinding.tvMinuteZeroth.text = zerothMin.toString()
                    }

                    if (secondsRemainingToDisplay <= 0) {
                        mBinding.tvSecondTenth.text = getString(R.string.label_default_hour)
                        mBinding.tvSecondZeroth.text = getString(R.string.label_default_hour)
                    } else {
                        val tenthSec = secondsRemainingToDisplay / 10
                        val zerothSec = secondsRemainingToDisplay % 10
                        mBinding.tvSecondTenth.text = tenthSec.toString()
                        mBinding.tvSecondZeroth.text = zerothSec.toString()
                    }
                }

                override fun onFinish() {
                    mBinding.tvSecondTenth.text = getString(R.string.label_default_hour)
                    mBinding.tvSecondZeroth.text = getString(R.string.label_default_hour)
                    mBinding.cpvTimer.progress = 100
                }

            }.start()
        }
    }


}