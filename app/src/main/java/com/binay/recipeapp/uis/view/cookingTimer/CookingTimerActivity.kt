package com.binay.recipeapp.uis.view.cookingTimer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowMetrics
import androidx.fragment.app.DialogFragment
import com.binay.recipeapp.R
import com.binay.recipeapp.databinding.ActivityCookingTimerBinding
import kotlin.math.roundToInt

/**
 * Activity that displays timer and sounds alarm once timer is completed
 */
class CookingTimerActivity : DialogFragment() {

    private lateinit var mBinding: ActivityCookingTimerBinding
    private var timeInMinutes: Int? = 40
    private var recipeName = "Test"
    private var isAlreadyLessThanHour = false
    private var countDownTimer: CountDownTimer? = null

    private val notificationId = System.currentTimeMillis().toInt()

    private var startTime: Long = 0
    private var sharedPreferences: SharedPreferences? = null
    private var firstTime = false

    fun newInstance(duration: Int, recipeName: String): CookingTimerActivity? {
        val fragment = CookingTimerActivity()
        val args = Bundle()
        args.putInt("ready_in_minutes", duration)
        args.putString("recipe_name", recipeName)
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = ActivityCookingTimerBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = context?.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

        startTime = sharedPreferences?.getLong("started_at", 0)!!

        if (startTime == 0.toLong()) {
            firstTime = true
            startTime = System.currentTimeMillis()
            val editor = sharedPreferences?.edit()
            editor?.putLong("started_at", startTime)
            editor?.apply()
        }

        timeInMinutes = arguments?.getInt("ready_in_minutes", 0)
        recipeName = arguments?.getString("recipe_name") ?: ""
        initView()
    }

    override fun onStart() {
        super.onStart()

        val screenWidth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = requireActivity().windowManager.currentWindowMetrics
            windowMetrics.bounds.width()
        } else {
            val display: Display = requireActivity().windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            size.x
        }

        dialog?.window?.let {
            it.setLayout(
                (screenWidth / 1.1).roundToInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    /**
     * Initializes view
     */
    private fun initView() {
        mBinding.close.setOnClickListener {
            dismiss()
        }

        mBinding.btnCancelCooking.visibility = View.GONE

        if (firstTime) {
            if (timeInMinutes != null) {
                startAlarm()
                val hours = timeInMinutes!! / 60
                if (hours <= 0) {
                    isAlreadyLessThanHour = true
                    mBinding.llHour.visibility = View.GONE
                }
                val progressMaxLimit = timeInMinutes!! * 60
                mBinding.cpvTimer.max = 100
                val timeInMilliSeconds = (timeInMinutes!! * 60 * 1000).toLong()
                var countDownInterval = 1
                countDownTimer =
                    object : CountDownTimer(timeInMilliSeconds, countDownInterval * 1000L) {
                        override fun onTick(p0: Long) {
                            countDownInterval += 1
                            val totalSecondsRemaining = p0 / 1000
                            val totalHoursRemaining = totalSecondsRemaining / (60 * 60)
                            val totalMinutesRemaining = if (totalHoursRemaining > 0) {
                                (totalSecondsRemaining / 60) - (totalHoursRemaining * 60)
                            } else {
                                totalSecondsRemaining / 60
                            }

                            val secondsRemainingToDisplay =
                                if (totalHoursRemaining > 0) {
                                    totalSecondsRemaining - (totalHoursRemaining * 60 * 60) - (totalMinutesRemaining * 60)
                                } else {
                                    totalSecondsRemaining - (totalMinutesRemaining * 60)
                                }

                            val progressPercentage = (100 * countDownInterval) / progressMaxLimit
                            mBinding.cpvTimer.progress = progressPercentage
                            Log.e("$countDownInterval Progress", " $progressPercentage%")
                            Log.e(
                                "Remaining ",
                                " Minutes: $totalMinutesRemaining Seconds: $totalSecondsRemaining"
                            )
                            if (!isAlreadyLessThanHour) {
                                if (totalHoursRemaining <= 0) {
                                    mBinding.tvHourTenth.text =
                                        getString(R.string.label_default_hour)
                                    mBinding.tvHourZeroth.text =
                                        getString(R.string.label_default_hour)
                                } else {
                                    val tenthHr = totalHoursRemaining / 10
                                    val zerothHr = totalHoursRemaining % 10
                                    mBinding.tvHourTenth.text = tenthHr.toString()
                                    mBinding.tvHourZeroth.text = zerothHr.toString()
                                }
                            }

                            if (totalMinutesRemaining <= 0) {
                                mBinding.tvMinuteTenth.text = getString(R.string.label_default_hour)
                                mBinding.tvMinuteZeroth.text =
                                    getString(R.string.label_default_hour)
                            } else {
                                val tenthMin = totalMinutesRemaining / 10
                                val zerothMin = totalMinutesRemaining % 10
                                mBinding.tvMinuteTenth.text = tenthMin.toString()
                                mBinding.tvMinuteZeroth.text = zerothMin.toString()
                            }

                            if (secondsRemainingToDisplay <= 0) {
                                mBinding.tvSecondTenth.text = getString(R.string.label_default_hour)
                                mBinding.tvSecondZeroth.text =
                                    getString(R.string.label_default_hour)
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
                            Log.e("Alarm ", "bajhna paryo")
                        }

                    }.start()

                mBinding.btnCancelCooking.setOnClickListener {
                    cancelAlarm()
                    dismiss()
                }
            }
        } else {
            val currentTime = System.currentTimeMillis()
            val elapsedTime = currentTime - startTime

            val timerInMillis: Long = (timeInMinutes!! * 60 * 1000).toLong()
            if (elapsedTime < timerInMillis) {
                val newTimeMilis = timerInMillis - elapsedTime
                val hours = newTimeMilis/ (1000 * 60 * 60)
                if (hours <= 0) {
                    isAlreadyLessThanHour = true
                    mBinding.llHour.visibility = View.GONE
                }
                val progressMaxLimit = newTimeMilis / 1000
                mBinding.cpvTimer.max = 100
                var countDownInterval = 1
                countDownTimer =
                    object : CountDownTimer(newTimeMilis, countDownInterval * 1000L) {
                        override fun onTick(p0: Long) {
                            countDownInterval += 1
                            val totalSecondsRemaining = p0 / 1000
                            val totalHoursRemaining = totalSecondsRemaining / (60 * 60)
                            val totalMinutesRemaining = if (totalHoursRemaining > 0) {
                                (totalSecondsRemaining / 60) - (totalHoursRemaining * 60)
                            } else {
                                totalSecondsRemaining / 60
                            }

                            val secondsRemainingToDisplay =
                                if (totalHoursRemaining > 0) {
                                    totalSecondsRemaining - (totalHoursRemaining * 60 * 60) - (totalMinutesRemaining * 60)
                                } else {
                                    totalSecondsRemaining - (totalMinutesRemaining * 60)
                                }

                            val progressPercentage = (100 * countDownInterval) / progressMaxLimit
                            mBinding.cpvTimer.progress = progressPercentage.toInt()
                            Log.e("$countDownInterval Progress", " $progressPercentage%")
                            Log.e(
                                "Remaining ",
                                " Minutes: $totalMinutesRemaining Seconds: $totalSecondsRemaining"
                            )
                            if (!isAlreadyLessThanHour) {
                                if (totalHoursRemaining <= 0) {
                                    mBinding.tvHourTenth.text =
                                        getString(R.string.label_default_hour)
                                    mBinding.tvHourZeroth.text =
                                        getString(R.string.label_default_hour)
                                } else {
                                    val tenthHr = totalHoursRemaining / 10
                                    val zerothHr = totalHoursRemaining % 10
                                    mBinding.tvHourTenth.text = tenthHr.toString()
                                    mBinding.tvHourZeroth.text = zerothHr.toString()
                                }
                            }

                            if (totalMinutesRemaining <= 0) {
                                mBinding.tvMinuteTenth.text = getString(R.string.label_default_hour)
                                mBinding.tvMinuteZeroth.text =
                                    getString(R.string.label_default_hour)
                            } else {
                                val tenthMin = totalMinutesRemaining / 10
                                val zerothMin = totalMinutesRemaining % 10
                                mBinding.tvMinuteTenth.text = tenthMin.toString()
                                mBinding.tvMinuteZeroth.text = zerothMin.toString()
                            }

                            if (secondsRemainingToDisplay <= 0) {
                                mBinding.tvSecondTenth.text = getString(R.string.label_default_hour)
                                mBinding.tvSecondZeroth.text =
                                    getString(R.string.label_default_hour)
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
                            Log.e("Alarm ", "bajhna paryo")
                        }

                    }.start()
            }
        }
    }

    /**
     * Sends call to AlarmReceiver to play sound
     */
    private fun startAlarm() {
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val timeWhenAlarmIsToBeSet = System.currentTimeMillis() + (timeInMinutes!! * 60 * 1000)
        intent.putExtra("timeWhenAlarmIsToBeSet", timeWhenAlarmIsToBeSet)
        intent.putExtra("recipeName", recipeName)
        intent.putExtra("notificationId", notificationId)
        intent.action = "startAlarm"
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager =
            requireContext().applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeWhenAlarmIsToBeSet, pendingIntent)
        Log.e("Alarm setting at ", " $timeWhenAlarmIsToBeSet")
    }

    /**
     * Cancels alarm
     */
    private fun cancelAlarm() {
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val timeWhenAlarmIsToBeSet = System.currentTimeMillis() + (timeInMinutes!! * 60 * 1000)
        intent.putExtra("timeWhenAlarmIsToBeSet", timeWhenAlarmIsToBeSet)
        intent.putExtra("recipeName", recipeName)
        intent.putExtra("notificationId", notificationId)
        intent.action = "startAlarm"
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = requireContext().applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    /**
     * Finishes activity
     */
//    private fun finishActivity() {
//        onBackPressedDispatcher.onBackPressed()
//    }

    /**
     * Cancel alarm and timer if already present
     */
    override fun onStop() {
        super.onStop()
        cancelAlarm()
        countDownTimer?.cancel()
    }

    companion object {
        const val ALARM_REQUEST_CODE = 123
    }

}