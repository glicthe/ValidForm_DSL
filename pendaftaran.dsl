FORMULIR 'AplikasiPendaftaran' {
    ISIAN nama lengkap {
        WAJIB;
        MIN_KARAKTER 5;
    }
    ISIAN umur {
        WAJIB;
        ANGKA_SAJA;
        MIN_NILAI 17;
    }
    ISIAN gaji harapan {
        OPSIONAL;
        DESIMAL;
    }
    ISIAN tanggal lahir {
        WAJIB;
        TANGGAL;
    }
    ISIAN email {
        WAJIB;
        EMAIL;
    }
    ISIAN password {
        WAJIB;
        MIN_KARAKTER 8;
        HARUS_HURUF_BESAR;
        HARUS_ADA_ANGKA;
        HARUS_ADA_SIMBOL;
    }
}