**Playground** is a work-in-progress Android application displaying places near you.

## Technologies stack
✅ **Hilt**  
➡ Simple DI with singleton Repositories and [Managers](https://github.com/C5FR7Q/Playground/blob/master/presentation/src/main/java/com/github/c5fr7q/playground/presentation/manager/ManagersAssembler.kt)  
➡ [Async inject](https://github.com/C5FR7Q/Playground/blob/1318b5639053e0c0e0b5a5ed0abc4fd96cca99b6/app/src/main/java/com/github/c5fr7q/playground/di/module/LocalSourceModule.kt#L25)

✅ **Multi module architecture**   
➡ Pure arch with 3 layers: presentation / domain / data

✅ **Coroutines + Flow**

✅ [**Handmade oversimplified MVI**](https://github.com/C5FR7Q/Playground/blob/master/presentation/src/main/java/com/github/c5fr7q/playground/presentation/ui/base/BaseViewModel.kt)  
➡ Full Intent, State, Side Effect support

✅ **Jetpack Compose**  
➡ [Lifecycled ViewModels](https://github.com/C5FR7Q/Playground/blob/1318b5639053e0c0e0b5a5ed0abc4fd96cca99b6/presentation/src/main/java/com/github/c5fr7q/playground/presentation/ui/Main.kt#L97)  
➡ [SideEffects processing](https://github.com/C5FR7Q/Playground/blob/1318b5639053e0c0e0b5a5ed0abc4fd96cca99b6/presentation/src/main/java/com/github/c5fr7q/playground/presentation/ui/screen/main/MainScreen.kt#L64)  
➡ [Dialogs processing](https://github.com/C5FR7Q/Playground/blob/1318b5639053e0c0e0b5a5ed0abc4fd96cca99b6/presentation/src/main/java/com/github/c5fr7q/playground/presentation/ui/Main.kt#L70)  
➡ Navigation  
➡ [Pagination](https://github.com/C5FR7Q/Playground/blob/1318b5639053e0c0e0b5a5ed0abc4fd96cca99b6/presentation/src/main/java/com/github/c5fr7q/playground/presentation/ui/screen/main/MainScreen.kt#L155)  
➡ [Local switch theme](https://github.com/C5FR7Q/Playground/blob/1318b5639053e0c0e0b5a5ed0abc4fd96cca99b6/presentation/src/main/java/com/github/c5fr7q/playground/presentation/ui/screen/common/selectable/SelectionScreen.kt#L101)  
➡ Animations  
➡ [Screen extension](https://github.com/C5FR7Q/Playground/blob/master/presentation/src/main/java/com/github/c5fr7q/playground/presentation/ui/screen/blocked/BlockedScreen.kt)

✅ **Retrofit**  
➡ Moshi

✅ **DataStore**

✅ **Clean Architecture**  
➡ [UseCases](https://github.com/C5FR7Q/Playground/tree/master/domain/src/main/java/com/github/c5fr7q/playground/domain/usecase)

✅ [**Common networking mechanism**](https://github.com/C5FR7Q/Playground/tree/master/data/src/main/java/com/github/c5fr7q/playground/data/source/remote/resourceprocessor)

## Features

### Main
❎ Displays places **near** you  
❎ Allows to display your favorite places only  
❎ Allows to select certain places category  
❎ Allows to manipulate with certain place: block, like / dislike, show in maps  
❎ Uses **all** persisted places on app launch if possible  
❎ Uses pagination in case of data was reloaded through the network

<img src="https://user-images.githubusercontent.com/17059563/129094137-76e85f79-3855-49c2-9170-208d72211a22.gif" width="270"><img src="https://user-images.githubusercontent.com/17059563/129094147-30cf01fa-30ec-4ca9-9f96-4aeec0316525.gif" width="270"><img src="https://user-images.githubusercontent.com/17059563/129094152-2eb44688-1269-4b67-b770-030514fd1636.gif" width="270"><img src="https://user-images.githubusercontent.com/17059563/129094161-947598a0-a7b7-49d4-8215-cccc1ce8492d.gif" width="270"><img src="https://user-images.githubusercontent.com/17059563/129094166-39c3b91a-8621-48b9-b3ad-8dec5521e1b3.gif" width="270"><img src="https://user-images.githubusercontent.com/17059563/129094175-ba41a54e-64cd-4302-af10-1792251e50a4.gif" width="270">



### Settings
❎ Displays app settings and allows to change them

<img src="https://user-images.githubusercontent.com/17059563/129094239-1b701d18-202d-4293-95ad-136df1862e63.gif" width="270">

### All liked places
❎ Displays **all** liked places  
❎ Allows to dislike selected places  
❎ Uses the same UI as for ***All blocked places***

<img src="https://user-images.githubusercontent.com/17059563/129094257-0d578b26-531d-4601-b28b-31e31533d8ba.gif" width="270">

### All blocked places
❎ Displays **all** blocked places  
❎ Allows to unblock selected places  
❎ Uses the same UI as for ***All liked places***

<img src="https://user-images.githubusercontent.com/17059563/129094281-3c68555f-f796-4fab-a012-91858d2c4ac7.gif" width="270">

## Future work
🔜 **Multi module architecture**  
➡ Feature modules  
➡ Dagger2 instead of Hilt

🔜 **Testing**  
➡ Unit  
➡ Integration  
➡ E2E

🔜 **KMM**  
➡ Android-independent ViewModels  
➡ Android-independent Manager abstractions  
➡ Ktor

🔜 **Gradle Kotlin DSL**

🔜 **JNI**

🔜 **WebSocket**

##

### 🎨[Figma](https://www.figma.com/file/No5xGyunuKLTa8rRd9cRKy/Playground?node-id=241%3A5865)

### 🎫[Trello](https://trello.com/b/h2tURjib/playground)

