# Android-Unslpash-s-Photo-Fetcher

<h3><u><b> Introduction</b></u> </h3>
<p> 
This application has used as a template the RecylerViewKotlin app into <a href="https://github.com/android/views-widgets-samples">The Official View Widgets Samples repository</a> maintained by the offical Android's dev team.
It have been made for a school project for my last engineer degree's year at Unilasalle Amiens between 01-18-2023 and 01-21-2023. 

>If you've planned to use this project as template, stay aware that many improvments (Threads managment, Retrofit2's callback, code duplication etc...) would have to be done due to the fact that it was a schoolar project made in 3 days.

</p>

<h3><u><b> How to use</b></u> </h3>

<p>

>Into your <u>gradle.properties</u> file, located at the project's root, create a registered value called : <b>UNSPLASH_API_KEY = "Bearer my_token_from_unsplash"</b>


>When you launch the app, the database clears all the entities that doesn't have their field 'is_cached' set at "true".
>Then with Retrofit2 we fetch 11 random photos, we store the urls with Sqlite and we can make it persist until we dislike the photo.
>Here's a global scheme of what are the steps to display the data that are stored into the database and then into the RecyclerView.

>![Data_Fetch_And_Save.drawio.png](Documentation%2FImages%2FData_Fetch_And_Save.drawio.png)

>When you launch the app you'll see the collection of images display into a RecyclerView
> ![Screenshot_20230121_204009_com.example.tpandroid_edit_73394267759112.jpg](Documentation%2FImages%2FScreenshot_20230121_204009_com.example.tpandroid_edit_73394267759112.jpg)

>When you click on the row that you want you'll enter into the detail activity wich looks like that :
> ![Screenshot_20230121_204034_com.example.tpandroid_edit_73463620315872.jpg](Documentation%2FImages%2FScreenshot_20230121_204034_com.example.tpandroid_edit_73463620315872.jpg)

>You can like/dislike a photo by clicking on the like button, it'll send again with Retrofit2 a POST/DELETE message to the Unsplash's API :
> ![Screenshot_20230121_204100_com.example.tpandroid_edit_73506530389303.jpg](Documentation%2FImages%2FScreenshot_20230121_204100_com.example.tpandroid_edit_73506530389303.jpg)

>You can delete any entry, it'll erase them from the database :
> ![Screenshot_20230121_204128_com.example.tpandroid_edit_73519934276280.jpg](Documentation%2FImages%2FScreenshot_20230121_204128_com.example.tpandroid_edit_73519934276280.jpg)

>Photos that are liked are saved at each new launch :
> ![Screenshot_20230121_204144_com.example.tpandroid_edit_73535648220549.jpg](Documentation%2FImages%2FScreenshot_20230121_204144_com.example.tpandroid_edit_73535648220549.jpg)
</p>
