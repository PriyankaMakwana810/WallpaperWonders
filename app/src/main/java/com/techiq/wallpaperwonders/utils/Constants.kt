package com.techiq.wallpaperwonders.utils

object Constants {

    const val KEY_IMAGE_CATEGORY = "image_type"
    const val KEY_COLLECTION_CATEGORY = "collection_category"
    const val KEY_PREVIEW_IMAGE_LINK = "preview_image_link"
    const val KEY_VIDEO_DOWNLOAD = "key_video_from_download"
    const val KEY_IMAGE_DOWNLOAD = "key_image_from_download"
    const val KEY_LARGE_IMAGE_LINK = "large_image_link"
    const val FROM = "from"
    const val KEY_IMAGE_ID = "image_id"

    const val PIXABAY_API_KEY = "38431804-1c9201a3537e9066943244f83"

    const val POWERED_BY = "PrefPoweredBy"
    const val POWERED_BY_PIXABAY = 0
    const val POWERED_BY_PEXELS = 1


    const val ORIENTATION = "portrait"
    const val SIZE = "large"
    const val AUTHORIZATION_KEY = "EZmQhJgvdWvdZWmNM2uxPcWLBvNmwmM97eO0Vc2X7k18RdCrDtthcJ79"
    const val KEY_VIDEO_CATEGORY = "video_category"
    const val KEY_VIDEO_ID = "video_id"
    const val PREF_VIDEO_URI = "videoUri"
    const val KEY_VIDEO_LINK = "video_link"


    object PexelsPhotos {
        fun getPexelsPhotos(): ArrayList<String> {
            val listOfPhotos = ArrayList<String>()
            listOfPhotos.addAll(
                listOf(
                    "Trending",
                    "NFTs",
                    "Anime",
                    "Logos",
                    "Bollywood",
                    "Games",
                    "Funny",
                    "Nature",
                    "Music",
                    "Patterns",
                    "Animals",
                    "Space",
                    "Sayings",
                    "People",
                    "Comics"
                )
            )
            return listOfPhotos
        }

        fun getPexelsVideos(): ArrayList<String> {
            val listOfVideos = ArrayList<String>()
            listOfVideos.addAll(
                listOf(
                    "Nature",
                    "Sky",
                    "Animals",
                    "Sunrise",
                    "Train",
                    "Historic Architecture",
                    "Planets",
                    "Galaxy",
                    "Moon"
                )
            )
            return listOfVideos
        }
    }


    object Pixabay {
        fun getPixabayList(): ArrayList<String> {
            val listNavigationItems = ArrayList<String>()
//            listNavigationItems.clear()
            listNavigationItems.add("Downloads")
            listNavigationItems.add("All")
            listNavigationItems.add("Nature")
            listNavigationItems.add("Fashion")
            listNavigationItems.add("Backgrounds")
            listNavigationItems.add("Science")
            listNavigationItems.add("Education")
            listNavigationItems.add("People")
            listNavigationItems.add("Feelings")
            listNavigationItems.add("Religion")
            listNavigationItems.add("Health")
            listNavigationItems.add("Places")
            listNavigationItems.add("Animals")
            listNavigationItems.add("Industry")
            listNavigationItems.add("Food")
            listNavigationItems.add("Computer")
            listNavigationItems.add("Sports")
            listNavigationItems.add("Transportation")
            listNavigationItems.add("Travel")
            listNavigationItems.add("Buildings")
            listNavigationItems.add("Business")
            listNavigationItems.add("Music")
            return listNavigationItems
        }
    }
}
