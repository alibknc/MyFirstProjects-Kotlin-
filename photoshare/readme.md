# Temel Fotoğraf Paylaşma Uygulaması - Basic Photo Share App

Firebase kullanılarak yaptığım uygulama Giriş ekranıyla açılmaktadır. Firebase Authentication ile email ve şifre kullanılarak oturum açma - kayıt olma işlemleri yapılmaktadır. Başarılı giriş sonucu açılan ana sayfada kullanıcıların yaptıkları paylaşımlar yer almaktadır. Sağ üst menüden çıkış yapma ve yeni paylaşım yapma seçenekleri mevcuttur. Paylaşım yapmak için açılan sayfada galeriden bir görsel seçilip yorumla birlikte gönderilmektedir. Firebase Storage ve Cloud Firestore ile paylaşılan gönderi ana sayfada en üste anlık olarak eklenmektedir.

Not: Uygulamayı çalıştırmak için Firebase panelinden aldığınız "google-services.json" dosyasını "app" klasörü altına yükleyiniz.

<img src="https://github.com/alibknc/MyFirstProjects-Kotlin-/blob/main/photoshare/screenshots/login.jpg" alt="Ana Sayfa - Home" width="200" height="400">
<img src="https://github.com/alibknc/MyFirstProjects-Kotlin-/raw/main/photoshare/screenshots/home.jpg" alt="Ana Sayfa - Home" width="200" height="400">
<img src="https://github.com/alibknc/MyFirstProjects-Kotlin-/raw/main/photoshare/screenshots/selectPhoto.jpg" alt="Yeni Gönderi - New Post" width="200" height="400">
<img src="https://github.com/alibknc/MyFirstProjects-Kotlin-/raw/main/photoshare/screenshots/comment.jpg" alt="Yorum - Comment" width="200" height="400">

The application I made using Firebase opens with the Home screen. With Firebase Authentication, login and registration processes are performed using email and password. The main page opened as a result of successful login includes the shares made by the users. There are options to sign out and make new shares from the top right menu. On the page that opens to share, an image is selected from the gallery and sent with the comment. The post shared with Firebase Storage and Cloud Firestore is instantly added to the top of the main page.

Note: To run the application, upload the "google-services.json" file you got from the Firebase panel under the "app" folder.
