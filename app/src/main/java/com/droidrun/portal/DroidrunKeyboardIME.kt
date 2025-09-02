package com.agent2droid.portal

import android.inputmethodservice.InputMethodService
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.ExtractedTextRequest

class Agent2DroidKeyboardIME : InputMethodService() {
    private val TAG = "Agent2DroidKeyboardIME"

    companion object {
        private var instance: Agent2DroidKeyboardIME? = null
        
        fun getInstance(): Agent2DroidKeyboardIME? = instance
        
        /**
         * Check if the Agent2DroidKeyboardIME is currently active and available
         */
        fun isAvailable(): Boolean = instance != null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.d(TAG, "Agent2DroidKeyboardIME: onCreate() called")
    }

    fun inputText(text: String): Boolean {
        return try {
            val ic = currentInputConnection
            if (ic != null) {
                ic.commitText(text, 1)
                Log.d(TAG, "Direct text input successful: $text")
                true
            } else {
                Log.w(TAG, "No input connection available for direct input")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in direct text input", e)
            false
        }
    }

    /**
     * Direct method to input text from Base64 without using broadcasts
     */
    fun inputB64Text(base64Text: String, append: Boolean = false): Boolean {
        return try {
            val decoded = Base64.decode(base64Text, Base64.DEFAULT)
            val text = String(decoded, Charsets.UTF_8)
            inputText(text, append)
        } catch (e: Exception) {
            Log.e(TAG, "Error decoding base64 for direct input", e)
            false
        }
    }
    
    fun inputText(text: String, append: Boolean = false): Boolean {
        return try {
            val ic = currentInputConnection
            if (ic != null) {
                if (!append) {
                    clearText()
                }
                ic.commitText(text, 0)
                Log.d(TAG, "Text input successful: $text (append=$append)")
                true
            } else {
                Log.w(TAG, "No input connection available for text input")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in text input", e)
            false
        }
    }
    

    /**
     * Direct method to clear text without using broadcasts
     */
    fun clearText(): Boolean {
        return try {
            val ic = currentInputConnection
            if (ic != null) {
                val extractedText = ic.getExtractedText(ExtractedTextRequest(), 0)
                if (extractedText != null) {
                    val curPos = extractedText.text
                    val beforePos = ic.getTextBeforeCursor(curPos.length, 0)
                    val afterPos = ic.getTextAfterCursor(curPos.length, 0)
                    ic.deleteSurroundingText(beforePos?.length ?: 0, afterPos?.length ?: 0)
                    Log.d(TAG, "Direct text clear successful")
                    true
                } else {
                    Log.w(TAG, "No extracted text available for clearing")
                    false
                }
            } else {
                Log.w(TAG, "No input connection available for direct clear")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in direct text clear", e)
            false
        }
    }

    /**
     * Direct method to send key events without using broadcasts
     */
    fun sendKeyEventDirect(keyCode: Int): Boolean {
        return try {
            val ic = currentInputConnection
            if (ic != null) {
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
                Log.d(TAG, "Direct key event sent: $keyCode")
                true
            } else {
                Log.w(TAG, "No input connection available for direct key event")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sending direct key event", e)
            false
        }
    }

    /**
     * Get current input connection status
     */
    fun hasInputConnection(): Boolean {
        return currentInputConnection != null
    }

    override fun onCreateInputView(): View {
        Log.d(TAG, "onCreateInputView called")
        
        // Inflate the existing keyboard layout XML
        return layoutInflater.inflate(R.layout.keyboard_view, null)
    }

    override fun onStartInput(attribute: android.view.inputmethod.EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        Log.d(TAG, "onStartInput called - restarting: $restarting")
    }

    override fun onStartInputView(attribute: android.view.inputmethod.EditorInfo?, restarting: Boolean) {
        super.onStartInputView(attribute, restarting)
        Log.d(TAG, "onStartInputView called - keyboard should be visible now")
    }

    override fun onDestroy() {
        Log.d(TAG, "Agent2DroidKeyboardIME: onDestroy() called")
        instance = null
        super.onDestroy()
    }
}