Welcome to the PantauHargaAndroidNative wiki!
## **DOKUMENTASI PANTAU HARGA**
![enter image description here](http://pantauharga.id/images/banana.png)

[PantauHarga](http://pantauharga.id/) adalah layanan situs web dan aplikasi untuk memantau harga komoditas pangan yang berada di sekitar pengguna. Pengguna pun dapat ikut serta melaporkan harga komoditasnya ketika sedang belanja di pasar tradisional atau supermarket.


Aplikasi ini menggunakan beberapa API dari Google, yaitu API Google Maps Android v2 . Cara pembuatan API Key dari Google Map tersebut bisa dilihat lengkap di : 
[https://developers.google.com/maps/documentation/android-api/intro](https://developers.google.com/maps/documentation/android-api/intro)

Aplikasi ini juga membutuhkan beberapa hal agar kode sumbernya bisa dibuka :

 - Android Studio   
 - Gradle versi 2.8 
 - Google Play Services API Maps

#### **Versi Redesain dan Optimasi**
```sh
1.0.0
```


----------
### **Link Server**
```sh
http://pantauharga.id
```

###**Content-Type** 
```json
application/json
```

----------

#### **Ambil Daftar Komoditas**
Ambil jenis komoditas pangan yang telah tersedia di server.
**GET (/Api/comodityall.json)**

**PARAMETER** :
Tidak ada
**RESPONSE**
```json
[{"id":1034,"name":"Bawang Merah Kering","sku":"35"},{"id":1044,"name":"Bawang Putih Impor","sku":"45"},{"id":1043,"name":"Bawang Putih Lokal","sku":"44"},{"id":958,"name":"Beras Ketan","sku":"3"},{"id":9,"name":"Beras Medium","sku":"1"},{"id":3791,"name":"Beras Organik","sku":"098"},{"id":10,"name":"Beras Pera","sku":"2"},{"id":8,"name":"Beras Premium","sku":"3"},{"id":1035,"name":"Cabe Merah Besar","sku":"36"},{"id":1036,"name":"Cabe Merah Keriting","sku":"37"},{"id":1045,"name":"Cabe Rawit Hijau","sku":"46"}]
```

----------

#### **Ambil Harga Komoditas Pangan**
Ambil harga komoditas pangan.
**POST (/Api/hargaall.json)**

**PARAMETER** :

Parameter  | Tipe | Keterangan
---------- | -----|-----
name       | String | nama komoditas
radius    | Integer | radius pencarian dalam kilometer
lat     | Double | koordinat latitude
Ing     | Double | koordinat longitude

Disusun dalam bentuk respon body String tipe JSON dengan contoh berikut :
```json
{"name":"Beras Medium","radius": "100","lat":-6.217,"lng":106.9}
```
**RESPONSE**
```json
[{"barang":"Beras Medium","errors":{"errors":[]},"lastUpdated":"2015-11-12T17:00:00Z","latitude":"-6.849407","longitude":"106.955305","nohp":"0","price":9700.0},{"barang":"Beras Medium","errors":{"errors":[]},"lastUpdated":"2015-10-03T02:09:21Z","latitude":"-6.2418255816301045","longitude":"106.87362134456635","nohp":"0","price":10000.0}]
```

----------

#### **Kirim Harga/Jual Komoditas Pangan**
Kirim harga komoditas pangan.
**POST (/Api/input.json)**

**PARAMETER** :

Parameter  | Tipe | Keterangan
---------- | -----|-----
id       | String | id komoditas komoditas
lat     | Double | koordinat latitude
Ing     | Double | koordinat longitude
nohp    |String  | nomor handphone pengguna atau pelapor
harga | Integer | harga komoditas pangan
quantity | Integer | jumlah komoditas yang tersedia

Disusun dalam bentuk respon body String tipe JSON dengan contoh berikut :
```json
{"id": "1034","lat":"-6.217","lng":"106.9","nohp":08123123,"harga":"20000","quantity":"30"}
```
**RESPONSE**
```json
{"errors":{"errors":[]},"harga":20000.0,"id":1034,"lat":-6.217,"lng":106.9,"nohp":"08123123","quantity":30}
```

----------

#### **Registrasi Pengguna/Pelapor Harga**
Pengguna yang belum terdaftar wajib mendaftarkan dirinya.

**POST (/Api/register.json)**

**PARAMETER** :

Parameter  | Tipe | Keterangan
---------- | -----|-----
nama | String | nama lengkap pengguna
username       | String | nama panggilan atau username untuk login
password     | String | password pengguna minimal 6 karakter
email     | String | alamat email pengguna
ktp    |String  | nomor KTP pengguna
nohp | String | nomor handphone pengguna
alamat | String | alamat lengkap rumah pengguna
kodepos | String | kode pos daerah tempat tinggal pengguna

Disusun dalam bentuk respon body String tipe JSON dengan contoh berikut :
```json
{"username":"username1s","password":"1234356","email":"emailss@mail.com","ktp": "123456","nohp":"1234567","alamat":"alamat tempat tinggal","kodepos":"50123","nama": "nama lengkap pengguna"}
```
**RESPONSE**
```json
{"alamat":"alamat tempat tinggal","email":"emailss@mail.com","errors":{"errors":[]},"kodepos":"50123","ktp":"123456","nama":"nama lengkap pengguna","nohp":"1234567","password":"1234356","username":"username1s"}
```

#### **Masuk ke Akun / Login**
Pengguna yang sudah terdaftar, bisa langsung masuk ke akun Pantau Harga.

**POST (/Api/login.json)**

**PARAMETER** :

|Parameter  | Tipe   | Keterangan                               |
|---------- | -------|------------------------------------------|
|username   | String | nama panggilan atau username untuk login |
|password   | String | password pengguna minimal 6 karakter     |


Disusun dalam bentuk respon body String tipe JSON dengan contoh berikut :
```json
{"username":"username1","password":"1234356"}
```
**RESPONSE**
```json
{"username":"saep","nama":"Saep Mahmudin","email":"saep@gmail.com","ktp":"123456","nohp":"1234567890","alamat":"Jalan Bandung Nomor 10","kodepos":"50121"}
```


----------


>### **Support Pantau Harga**
> 1. [Facebook Pantau Harga](https://www.facebook.com/pantauharga)
 >2. [Twitter Pantau Harga](https://twitter.com/pantauharga)
 >3. [Web Pantau Harga](http://pantauharga.id/)
 >4. Kontak Email **[pantauharga@gmail.com](pantauharga@gmail.com)**