package com.wuqi.a_gpuimage.di

import android.os.Environment
import com.blankj.utilcode.util.FileUtils
import com.weyee.poscore.di.scope.ActivityScope
import com.weyee.poscore.mvp.BaseModel
import com.weyee.poscore.mvp.BasePresenter
import com.weyee.poscore.mvp.IView
import com.weyee.sdk.medialoader.filter.PhotoFilter
import java.io.File
import javax.inject.Inject

/**
 *
 * @author wuqi by 2019-08-15.
 */
@ActivityScope
class ImageHandlePresenter @Inject constructor(rootView: IView?) : BasePresenter<BaseModel, IView>(rootView) {
    fun getFunction(): List<String> {
        val list = mutableListOf<String>()

        list.add("GPUImage3x3ConvolutionFilter")
        list.add("GPUImage3x3TextureSamplingFilter")
        list.add("GPUImageAddBlendFilter")
        list.add("GPUImageAlphaBlendFilter")
        list.add("GPUImageBilateralBlurFilter")
        list.add("GPUImageBoxBlurFilter")
        list.add("GPUImageBrightnessFilter")
        list.add("GPUImageBulgeDistortionFilter")
        list.add("GPUImageCGAColorspaceFilter")
        list.add("GPUImageChromaKeyBlendFilter")
        list.add("GPUImageColorBalanceFilter")
        list.add("GPUImageColorBlendFilter")
        list.add("GPUImageColorBurnBlendFilter")
        list.add("GPUImageColorDodgeBlendFilter")
        list.add("GPUImageColorInvertFilter")
        list.add("GPUImageColorMatrixFilter")
        list.add("GPUImageContrastFilter")
        list.add("GPUImageCrosshatchFilter")
        list.add("GPUImageDarkenBlendFilter")
        list.add("GPUImageDifferenceBlendFilter")
        list.add("GPUImageDilationFilter")
        list.add("GPUImageDirectionalSobelEdgeDetectionFilter")
        list.add("GPUImageDissolveBlendFilter")
        list.add("GPUImageDivideBlendFilter")
        list.add("GPUImageEmbossFilter")
        list.add("GPUImageExclusionBlendFilter")
        list.add("GPUImageExposureFilter")
        list.add("GPUImageFalseColorFilter")
        list.add("GPUImageFilter")
        list.add("GPUImageFilterGroup")
        list.add("GPUImageGammaFilter")
        list.add("GPUImageGaussianBlurFilter")
        list.add("GPUImageGlassSphereFilter")
        list.add("GPUImageHalftoneFilter")
        list.add("GPUImageHardLightBlendFilter")
        list.add("GPUImageHazeFilter")
        list.add("GPUImageHighlightShadowFilter")
        list.add("GPUImageHueBlendFilter")
        list.add("GPUImageHueFilter")
        list.add("GPUImageKuwaharaFilter")
        list.add("GPUImageLevelsFilter")
        list.add("GPUImageLightenBlendFilter")
        list.add("GPUImageLinearBurnBlendFilter")
        list.add("GPUImageLookupFilter")
        list.add("GPUImageLuminanceFilter")
        list.add("GPUImageLuminanceThresholdFilter")  // 黑白处理
        list.add("GPUImageLuminosityBlendFilter")
        list.add("GPUImageMixBlendFilter")
        list.add("GPUImageMonochromeFilter")
        list.add("GPUImageMultiplyBlendFilter")
        list.add("GPUImageNonMaximumSuppressionFilter")
        list.add("GPUImageNormalBlendFilter")
        list.add("GPUImageOpacityFilter")
        list.add("GPUImageOverlayBlendFilter")
        list.add("GPUImagePixelationFilter")
        list.add("GPUImagePosterizeFilter")
        list.add("GPUImageRGBDilationFilter")
        list.add("GPUImageRGBFilter")
        list.add("GPUImageSaturationBlendFilter")
        list.add("GPUImageSaturationFilter")
        list.add("GPUImageScreenBlendFilter")
        list.add("GPUImageSepiaToneFilter")
        list.add("GPUImageSharpenFilter")
        list.add("GPUImageSketchFilter")
        list.add("GPUImageSmoothToonFilter")
        list.add("GPUImageSobelEdgeDetectionFilter")
        list.add("GPUImageSobelThresholdFilter")  // 这个也是可以的
        list.add("GPUImageSoftLightBlendFilter")
        list.add("GPUImageSolarizeFilter")
        list.add("GPUImageSourceOverBlendFilter")
        list.add("GPUImageSphereRefractionFilter")
        list.add("GPUImageSubtractBlendFilter")
        list.add("GPUImageSwirlFilter")
        list.add("GPUImageThresholdEdgeDetectionFilter")  // 这个可以试试
        list.add("GPUImageToneCurveFilter")
        list.add("GPUImageToonFilter")  // 这个也是可以的
        list.add("GPUImageTransformFilter")
        list.add("GPUImageTwoInputFilter")
        list.add("GPUImageTwoPassFilter")
        list.add("GPUImageTwoPassTextureSamplingFilter")
        list.add("GPUImageVibranceFilter")
        list.add("GPUImageVignetteFilter")
        list.add("GPUImageWeakPixelInclusionFilter")
        list.add("GPUImageWhiteBalanceFilter")

        return list
    }

    fun getFiles() : List<File>{
        return FileUtils.listFilesInDirWithFilter("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/GPUImage",
            PhotoFilter()
        )
    }
}