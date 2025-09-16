package com.sagarpaliwal_task.presentation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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
        
        // Animate button
        val buttonAnimator = ObjectAnimator.ofFloat(binding.btnViewPortfolio, "scaleX", 0f, 1f)
        val buttonAnimatorY = ObjectAnimator.ofFloat(binding.btnViewPortfolio, "scaleY", 0f, 1f)
        buttonAnimator.duration = 600
        buttonAnimatorY.duration = 600
        buttonAnimator.startDelay = 600
        buttonAnimatorY.startDelay = 600
        buttonAnimator.interpolator = AccelerateDecelerateInterpolator()
        buttonAnimatorY.interpolator = AccelerateDecelerateInterpolator()
        
        // Start animations
        headerAnimator.start()
        cardAnimator.start()
        featuresAnimator.start()
        buttonAnimator.start()
        buttonAnimatorY.start()
    }
    
    private fun setupClickListeners() {
        binding.btnViewPortfolio.setOnClickListener {
            // Add button press animation
            val scaleDownX = ObjectAnimator.ofFloat(binding.btnViewPortfolio, "scaleX", 1f, 0.95f)
            val scaleDownY = ObjectAnimator.ofFloat(binding.btnViewPortfolio, "scaleY", 1f, 0.95f)
            val scaleUpX = ObjectAnimator.ofFloat(binding.btnViewPortfolio, "scaleX", 0.95f, 1f)
            val scaleUpY = ObjectAnimator.ofFloat(binding.btnViewPortfolio, "scaleY", 0.95f, 1f)
            
            scaleDownX.duration = 100
            scaleDownY.duration = 100
            scaleUpX.duration = 100
            scaleUpY.duration = 100
            
            val pressAnimation = AnimatorSet()
            pressAnimation.play(scaleDownX).with(scaleDownY)
            pressAnimation.play(scaleUpX).with(scaleUpY).after(scaleDownX)
            pressAnimation.start()
            
            // Navigate to HoldingsActivity
            val intent = Intent(this, HoldingsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}