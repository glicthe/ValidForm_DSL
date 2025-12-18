grammar ValidForm;

// --- Struktur Utama ---
fileDsl : formulir EOF ;

formulir : 'FORMULIR ' STRING '{' isian* '}' ;

isian : 'ISIAN ' ID+ '{' aturan* '}' ;

// --- Aturan Validasi ---
aturan
    // Aturan Dasar
    : (WAJIB_DIISI | TIDAK_WAJIB) ';'
    
    // Aturan Tipe Data Khusus
    | (FORMAT_EMAIL | FORMAT_TANGGAL | FORMAT_DESIMAL | HANYA_ANGKA) ';'
    
    // Aturan Numerik/Panjang
    | (MINIMAL_KARAKTER | MAKSIMAL_KARAKTER | LEBIH_BESAR_DARI) ANGKA ';'
    
    // Aturan Ekspresif Password (Menyembunyikan Regex)
    | (HARUS_HURUF_BESAR | HARUS_HURUF_KECIL | HARUS_ANGKA | HARUS_SIMBOL) ';'
    ;

// --- Lexer (Keyword Bahasa Indonesia) ---
FORMULIR : 'FORMULIR' ;
ISIAN    : 'ISIAN' ;

// Validasi
WAJIB_DIISI      : 'WAJIB' ;
TIDAK_WAJIB      : 'OPSIONAL' ;
MINIMAL_KARAKTER : 'MIN_KARAKTER' ;
MAKSIMAL_KARAKTER: 'MAX_KARAKTER' ;
LEBIH_BESAR_DARI : 'MIN_NILAI' ;

// Format & Tipe
HANYA_ANGKA      : 'ANGKA_SAJA' ;
FORMAT_EMAIL     : 'EMAIL' ;
FORMAT_TANGGAL   : 'TANGGAL' ; // format dd-mm-yyyy
FORMAT_DESIMAL   : 'DESIMAL' ; // angka dengan koma/titik

// Password Expressiveness
HARUS_HURUF_BESAR : 'HARUS_HURUF_BESAR' ;
HARUS_HURUF_KECIL : 'HARUS_HURUF_KECIL' ;
HARUS_ANGKA       : 'HARUS_ADA_ANGKA' ;
HARUS_SIMBOL      : 'HARUS_ADA_SIMBOL' ;

ID     : [a-zA-Z_][a-zA-Z0-9_]* ; 
ANGKA  : [0-9]+ ;
STRING : '\'' ( ~('\''|'\\') | '\\' . )* '\'' ;
KOMENTAR : '//' .*? '\n' -> skip ;
WS       : [ \t\r\n]+ -> skip ;