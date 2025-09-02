package com.agent2droid.portal.model

import android.view.accessibility.AccessibilityNodeInfo

data class PhoneState (
    val focusedElement: AccessibilityNodeInfo?,
    val keyboardVisible: Boolean,
    val packageName: String?,
    val appName: String?
)
