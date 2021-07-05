package com.github.c5fr7q.playground.presentation.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.github.c5fr7q.playground.presentation.R

private val appFontFamily = FontFamily(
	listOf(
		Font(resId = R.font.montserrat_thin, weight = FontWeight(100), style = FontStyle.Normal),
		Font(resId = R.font.montserrat_thin_italic, weight = FontWeight(100), style = FontStyle.Italic),
		Font(resId = R.font.montserrat_extra_light, weight = FontWeight(200), style = FontStyle.Normal),
		Font(resId = R.font.montserrat_extra_light_italic, weight = FontWeight(200), style = FontStyle.Italic),
		Font(resId = R.font.montserrat_light, weight = FontWeight(300), style = FontStyle.Normal),
		Font(resId = R.font.montserrat_light_italic, weight = FontWeight(300), style = FontStyle.Italic),
		Font(resId = R.font.montserrat_regular, weight = FontWeight(400), style = FontStyle.Normal),
		Font(resId = R.font.montserrat_italic, weight = FontWeight(400), style = FontStyle.Italic),
		Font(resId = R.font.montserrat_medium, weight = FontWeight(500), style = FontStyle.Normal),
		Font(resId = R.font.montserrat_medium_italic, weight = FontWeight(500), style = FontStyle.Italic),
		Font(resId = R.font.montserrat_semi_bold, weight = FontWeight(600), style = FontStyle.Normal),
		Font(resId = R.font.montserrat_semi_bold_italic, weight = FontWeight(600), style = FontStyle.Italic),
		Font(resId = R.font.montserrat_bold, weight = FontWeight(700), style = FontStyle.Normal),
		Font(resId = R.font.montserrat_bold_italic, weight = FontWeight(700), style = FontStyle.Italic),
		Font(resId = R.font.montserrat_extra_bold, weight = FontWeight(800), style = FontStyle.Normal),
		Font(resId = R.font.montserrat_extra_bold_italic, weight = FontWeight(800), style = FontStyle.Italic),
		Font(resId = R.font.montserrat_black, weight = FontWeight(900), style = FontStyle.Normal),
		Font(resId = R.font.montserrat_black_italic, weight = FontWeight(900), style = FontStyle.Italic),
	)
)

// Set of Material typography styles to start with
val Typography = Typography(defaultFontFamily = appFontFamily)