package com.sagarpaliwal_task.presentation.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.sagarpaliwal_task.R
import com.sagarpaliwal_task.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handle system UI for edge-to-edge display
        setupSystemUI()
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupAnimations()
        setupClickListeners()
    }
    
    @SuppressLint("NewApi")
    private fun setupSystemUI() {
        // Make status bar transparent to show gradient background
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        
        // Make status bar content light (for white status bar icons)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = false
    }
    
    private fun setupAnimations() {
        // Animate header section
        val headerAnimator = ObjectAnimator.ofFloat(binding.headerSection, "translationY", -200f, 0f)
        headerAnimator.duration = 800
        headerAnimator.interpolator = DecelerateInterpolator()
        
        // Animate main card
        val cardAnimator = ObjectAnimator.ofFloat(binding.mainCard, "translationY", 200f, 0f)
        cardAnimator.duration = 800
        cardAnimator.interpolator = DecelerateInterpolator()
        
        // Animate features section
        val featuresAnimator = ObjectAnimator.ofFloat(binding.featuresSection, "alpha", 0f, 1f)
        featuresAnimator.duration = 1000
        featuresAnimator.startDelay = 400
        
        // Animate buttons
        val xmlButtonAnimator = ObjectAnimator.ofFloat(binding.btnViewPortfolioXml, "scaleX", 0f, 1f)
        val xmlButtonAnimatorY = ObjectAnimator.ofFloat(binding.btnViewPortfolioXml, "scaleY", 0f, 1f)
        val composeButtonAnimator = ObjectAnimator.ofFloat(binding.btnViewPortfolioCompose, "scaleX", 0f, 1f)
        val composeButtonAnimatorY = ObjectAnimator.ofFloat(binding.btnViewPortfolioCompose, "scaleY", 0f, 1f)
        
        xmlButtonAnimator.duration = 600
        xmlButtonAnimatorY.duration = 600
        composeButtonAnimator.duration = 600
        composeButtonAnimatorY.duration = 600
        xmlButtonAnimator.startDelay = 600
        xmlButtonAnimatorY.startDelay = 600
        composeButtonAnimator.startDelay = 700
        composeButtonAnimatorY.startDelay = 700
        
        xmlButtonAnimator.interpolator = AccelerateDecelerateInterpolator()
        xmlButtonAnimatorY.interpolator = AccelerateDecelerateInterpolator()
        composeButtonAnimator.interpolator = AccelerateDecelerateInterpolator()
        composeButtonAnimatorY.interpolator = AccelerateDecelerateInterpolator()

        // Start animations
        headerAnimator.start()
        cardAnimator.start()
        featuresAnimator.start()
        xmlButtonAnimator.start()
        xmlButtonAnimatorY.start()
        composeButtonAnimator.start()
        composeButtonAnimatorY.start()
    }
    
    private fun setupClickListeners() {
        // XML UI Button
        binding.btnViewPortfolioXml.setOnClickListener { button ->
            animateButtonPress(button) {
                // Navigate to XML HoldingsActivity
                val intent = Intent(this, HoldingsActivity::class.java)
                intent.putExtra("ui_mode", "xml")
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
        
        // Compose UI Button
        binding.btnViewPortfolioCompose.setOnClickListener { button ->
            animateButtonPress(button) {
                // Navigate to Compose HoldingsActivity
                val intent = Intent(this, HoldingsComposeActivity::class.java)
                intent.putExtra("ui_mode", "compose")
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
    }
    
    private fun animateButtonPress(button: View, onComplete: () -> Unit) {
        // Add button press animation
        val scaleDownX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.95f)
        val scaleDownY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.95f)
        val scaleUpX = ObjectAnimator.ofFloat(button, "scaleX", 0.95f, 1f)
        val scaleUpY = ObjectAnimator.ofFloat(button, "scaleY", 0.95f, 1f)
        
        scaleDownX.duration = 100
        scaleDownY.duration = 100
        scaleUpX.duration = 100
        scaleUpY.duration = 100
        
        val pressAnimation = AnimatorSet()
        pressAnimation.play(scaleDownX).with(scaleDownY)
        pressAnimation.play(scaleUpX).with(scaleUpY).after(scaleDownX)
        pressAnimation.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                onComplete()
            }
        })
        pressAnimation.start()
    }
}