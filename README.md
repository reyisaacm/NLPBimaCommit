Cara menggunakan:

Aplikasi ini berjalan pada sistem operasi Linux. Gunakan distro Linux Mint 18/Ubuntu 16.04

Unduh file-file kaldi:
https://drive.google.com/drive/folders/0B1HBMRSXB7EtekN1RUpiMGtWeE0?usp=sharing
dan extract ke folder root dari project


Langkah-langkah untuk meng-compile kaldi
1. Buka termninal pada kaldi/tools/extra
2. Masukan perintah berikut:
	- chmod +x ./check_dependencies.sh
	- sudo ./check_dependencies.sh
3. Lakukan instalasi tambahan sesuai hasil dari check_dependencies.sh
4. Buka termninal pada kaldi/src/
5a. Masukan perintah-perintah berikut:
	- make clean
	- ./configure --shared
	- make depend -j <jumlah core processor di komputer>
	- make -j <jumlah core processor di komputer>
	- sudo apt-get install gstreamer1.0-plugins-bad  gstreamer1.0-plugins-base gstreamer1.0-plugins-good  gstreamer1.0-pulseaudio  gstreamer1.0-plugins-ugly  gstreamer1.0-tools libgstreamer1.0-dev
	- sudo apt-get install libjansson-dev
	- make ext -j <jumlah core processor di komputer>
5b. Buka terminal pada kaldi/src/gst-plugin. Dan masukan perintah-perintah berikut:
	- make clean
	- make depend -j <jumlah core processor di komputer>
	- make -j <jumlah core processor di komputer>
6. Buka terminal pada kaldi/tools/gst-kaldi-nnet2-online/src
7. Masukan perintah-perintah berikut:
	- export KALDI_ROOT=<absolute path ke kaldi src>
	- make depend -j <jumlah core processor di komputer>
	- make -j <jumlah core processor di komputer>
8. Lakukan pengecekan apakah sudah jalan dengan menggunakan perintah berikut:
	- export GST_PLUGIN_PATH=<absolute path ke gst-kaldi-nnet2-online/src>
	- gst-inspect-1.0 kaldinnet2onlinedecoder

Langkah-langkah untuk menjalankan server kaldi
1. Pastikan bahwa komponen-komponen berikut telah terinstall
	- Python
	- Python-pip
	- Python setup tools
	- Python wheels
	- Python-gi
	- Tornado (www.tornadoweb.org/en/stable)
	- ws4py
	- YAML
	- JSON
2. Buka terminal pada kaldi-gstreamer-server. Jalankan server dengan menggunakan:
	- python kaldigstserver/master_server.py --port=8888
3. Jalankan worker dengan menggunakan:
	- export GST_PLUGIN_PATH=<absolute path ke gst-kaldi-nnet2-online/src>
	- python kaldigstserver/worker.py -u ws://localhost:8888/worker/ws/speech -c indonesia-worker-gmm.yaml
4. Check apakah server berjalan atau tidak dengan menggunakan WebSocket client
	- masukkan alamat ws://localhost:8080/client/ws/status

Langkah-langkah untuk menjalankan:
1. Lakukan instalasi Netbeans 8.2
2. Pada Netbeans pilih open project, dan open project yang sudah di download/clone
3. Tekan run pada netbeans
