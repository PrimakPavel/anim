/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.propertyanimation

import android.animation.*
import android.graphics.Color
import android.os.Bundle
import android.util.Property
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView


private const val DELTA_SIZE = 100
private const val DURATION = 1000L

class MainActivity : AppCompatActivity() {
    private val star by lazy { findViewById<ImageView>(R.id.star) }
    private val rotateButton by lazy { findViewById<Button>(R.id.rotateButton) }
    private val translateButton by lazy { findViewById<Button>(R.id.translateButton) }
    private val scaleButton by lazy { findViewById<Button>(R.id.scaleButton) }
    private val fadeButton by lazy { findViewById<Button>(R.id.fadeButton) }
    private val colorizeButton by lazy { findViewById<Button>(R.id.colorizeButton) }
    private val showerButton by lazy { findViewById<Button>(R.id.showerButton) }
    private val collapsedButton by lazy { findViewById<Button>(R.id.collapseButton) }

    private val collapsedSize by lazy { star.height }
    private val expandedSize by lazy { collapsedSize + DELTA_SIZE }

    private var isCollapsed = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rotateButton.setOnClickListener { rotater(star) }

        translateButton.setOnClickListener { translater(star) }

        scaleButton.setOnClickListener { scaler(star) }

        fadeButton.setOnClickListener { fader(star) }

        colorizeButton.setOnClickListener { colorizer(star) }

        showerButton.setOnClickListener { shower() }

        collapsedButton.setOnClickListener {
            isCollapsed = if (isCollapsed) {
                animateHeightTo(star, expandedSize)
                false
            } else {
                animateHeightTo(star, collapsedSize)
                true
            }
        }
    }

    private fun rotater(view: View) {
        val animator = ObjectAnimator.ofFloat(view, View.ROTATION, -360f, 0f)
        animator.duration = DURATION
        animator.disableViewDuringAnimation(rotateButton)
        animator.start()
    }

    private fun translater(view: View) {
        val animator = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, -200f)
        animator.repeatCount = 3
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.disableViewDuringAnimation(translateButton)
        animator.start()
    }

    private fun scaler(view: View) {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 4f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 4f)
        val translationX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 300f)
        val translationY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 300f)
        val animator = ObjectAnimator.ofPropertyValuesHolder(
            view, translationX, translationY, scaleX, scaleY
        )
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.disableViewDuringAnimation(scaleButton)
        animator.start()
    }

    private fun fader(view: View) {
        val animator = ObjectAnimator.ofFloat(view, View.ALPHA, 0f)
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.disableViewDuringAnimation(fadeButton)
        animator.start()
    }

    private fun colorizer(view: View) {
        val animator = ObjectAnimator.ofArgb(view.parent, "backgroundColor", Color.BLACK, Color.RED)
        animator.duration = DURATION
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.disableViewDuringAnimation(colorizeButton)
        animator.start()
    }

    private fun shower() {
        val container = star.parent as ViewGroup
        val containerW = container.width
        val containerH = container.height
        var starW: Float = star.width.toFloat()
        var starH: Float = star.height.toFloat()


        val newStar = AppCompatImageView(this)
        newStar.setImageResource(R.drawable.ic_star)
        newStar.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        container.addView(newStar)

        //Start size
        newStar.scaleX = Math.random().toFloat() * 1.6f + .1f
        newStar.scaleY = newStar.scaleX
        starW *= newStar.scaleX
        starH *= newStar.scaleY
        //Start position
        newStar.translationX = Math.random().toFloat() *
                containerW - starW / 2
        //Animation
        val mover = ObjectAnimator.ofFloat(newStar, View.TRANSLATION_Y, -starH, containerH + starH)
        mover.interpolator = AccelerateInterpolator(1f)
        val rotator =
            ObjectAnimator.ofFloat(newStar, View.ROTATION, (Math.random() * 1080).toFloat())
        rotator.interpolator = LinearInterpolator()
        val set = AnimatorSet()
        set.playTogether(mover, rotator)
        set.duration = (Math.random() * 1500 + 500).toLong()
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                container.removeView(newStar)
            }
        })
        set.start()
    }

    private fun animateHeightTo(view: View, height: Int) {
        val currentHeight = star.height
        val animator = ObjectAnimator.ofInt(view, HeightProperty(), currentHeight, height)
        animator.duration = 300
        animator.interpolator = DecelerateInterpolator()
        animator.disableViewDuringAnimation(collapsedButton)
        animator.start()
    }

    private fun ObjectAnimator.disableViewDuringAnimation(view: View) {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                view.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                view.isEnabled = true
            }
        })
    }


    internal class HeightProperty : Property<View, Int>(Int::class.java, "height") {

        override fun get(view: View): Int {
            return view.height
        }

        override fun set(view: View, value: Int) {
            view.layoutParams.height = value
            view.layoutParams = view.layoutParams
        }
    }
}
