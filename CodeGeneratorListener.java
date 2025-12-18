import org.antlr.v4.runtime.tree.TerminalNode;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CodeGeneratorListener extends ValidFormBaseListener {
    
    // --- 1. Template Engine Sederhana (Map Based) ---
    private Map<String, String> templates = new HashMap<>();
    
    private String className;
    private StringBuilder inputLogic = new StringBuilder();

    public CodeGeneratorListener() {
        // Template untuk Kerangka Kelas (Aplikasi Interaktif)
        templates.put("CLASS_SKELETON", 
            "import java.util.Scanner;\n" +
            "import java.util.regex.Pattern;\n\n" +
            "public class %CLASS_NAME% {\n" +
            "    public static void main(String[] args) {\n" +
            "        Scanner scanner = new Scanner(System.in);\n" +
            "        System.out.println(\"=== FORMULIR: %CLASS_NAME% ===\");\n\n" +
            "        // Variables\n" +
            "%VARIABLES%\n\n" +
            "        // Input Loops\n" +
            "%LOGIC%\n" +
            "        System.out.println(\"\\n=== REGISTRASI BERHASIL ===\");\n" +
            "        System.out.println(\"Data tersimpan.\");\n" +
            "    }\n" +
            "}\n"
        );

        // Template untuk Logika Input Per Field (Looping sampai valid)
        templates.put("INPUT_BLOCK", 
            "        while (true) {\n" +
            "            System.out.print(\"Masukkan %FIELD_LABEL%: \");\n" +
            "            \n" +
            "            // Membaca Input (Biasa atau Password)\n" +
            "%INPUT_METHOD%" + 
            "            \n" +
            "            // Validasi\n" +
            "            String error = null;\n" +
            "%VALIDATIONS%" +
            "            \n" +
            "            if (error != null) {\n" +
            "                System.out.println(\" [x] Error: \" + error);\n" +
            "            } else {\n" +
            "                break;\n" +
            "            }\n" +
            "        }\n\n"
        );
    }

    // --- 2. Implementasi Listener ---

    @Override
    public void enterFormulir(ValidFormParser.FormulirContext ctx) {
        // Ambil nama formulir dari DSL
        className = ctx.STRING().getText().replace("'", "").replace(" ", "");
    }

    @Override
    public void enterIsian(ValidFormParser.IsianContext ctx) {
        // --- UNTUK PENGGABUNGAN SPASI ---
        
        // 1. Ambil semua token ID (misal: "nama", "lengkap")
        StringBuilder sb = new StringBuilder();
        for (TerminalNode token : ctx.ID()) { 
            if (sb.length() > 0) sb.append(" ");
            sb.append(token.getText());
        }
        
        // 2. Hasilnya sekarang: "nama lengkap" (String utuh dengan spasi)
        String originalName = sb.toString();

        String varName = originalName.replace(" ", "_").toLowerCase(); // nama_lengkap
        String labelName = originalName; // nama lengkap

        // Siapkan logika validasi untuk field ini
        Boolean isPassword = varName.contains("password") || varName.contains("sandi");
        StringBuilder validations = new StringBuilder();

        for (ValidFormParser.AturanContext aturan : ctx.aturan()) {
            
            // --- Translasi DSL ke Java Logic ---
            
            if (aturan.WAJIB_DIISI() != null) {
                validations.append("            if (").append(varName).append(".trim().isEmpty()) error = \"Wajib diisi!\";\n");
            }
            
            if (aturan.TIDAK_WAJIB() != null) {
                validations.append("            if (").append(varName).append(".trim().isEmpty()) break;\n");
            }
            
            if (aturan.MINIMAL_KARAKTER() != null) {
                String min = aturan.ANGKA().getText();
                validations.append("            else if (").append(varName).append(".length() < ").append(min).append(") error = \"Minimal ").append(min).append(" karakter\";\n");
            }
            
            // Masalah Regex disembunyikan di sini. User DSL hanya tulis 'ANGKA_SAJA'
            if (aturan.HANYA_ANGKA() != null) {
                validations.append("            else if (!").append(varName).append(".matches(\"\\\\d+\")) error = \"Harus berupa angka\";\n");
            }
            
            if (aturan.FORMAT_EMAIL() != null) {
                validations.append("            else if (!").append(varName).append(".matches(\"^[\\\\w-\\\\.]+@([\\\\w-]+\\\\.)+[\\\\w-]{2,4}$\")) error = \"Format email salah\";\n");
            }

            // Fitur Baru: Tanggal (DD-MM-YYYY)
            if (aturan.FORMAT_TANGGAL() != null) {
                validations.append("            else if (!").append(varName).append(".matches(\"^\\\\d{2}-\\\\d{2}-\\\\d{4}$\")) error = \"Format tanggal harus DD-MM-YYYY\";\n");
            }

            // Fitur Baru: Desimal
            if (aturan.FORMAT_DESIMAL() != null) {
                validations.append("            else if (!").append(varName).append(".matches(\"^[0-9]+(\\\\.[0-9]+)?$\")) error = \"Harus format desimal (contoh: 10.5)\";\n");
            }

            if (aturan.LEBIH_BESAR_DARI() != null) {
                String val = aturan.ANGKA().getText();
                // Validasi angka dulu baru cek value
                validations.append("            else if (").append(varName).append(".matches(\"\\\\d+\") && Integer.parseInt(").append(varName).append(") <= ").append(val).append(") error = \"Harus lebih besar dari ").append(val).append("\";\n");
            }

            // --- Password Expressiveness (User tidak perlu tahu regex) ---
            if (aturan.HARUS_HURUF_BESAR() != null) {
                validations.append("            else if (!").append(varName).append(".matches(\".*[A-Z].*\")) error = \"Password harus mengandung huruf besar\";\n");
            }
            if (aturan.HARUS_ANGKA() != null) {
                validations.append("            else if (!").append(varName).append(".matches(\".*[0-9].*\")) error = \"Password harus mengandung angka\";\n");
            }
            if (aturan.HARUS_SIMBOL() != null) {
                validations.append("            else if (!").append(varName).append(".matches(\".*[^a-zA-Z0-9].*\")) error = \"Password harus mengandung simbol unik\";\n");
            }
        }
        
        String inputMethodCode;
        
        if (isPassword) {
            // Jika Password: Gunakan System.console() agar tidak terlihat
            // Kita beri fallback ke scanner jika System.console() null (saat di IDE)
            inputMethodCode = 
                "            if (System.console() != null) {\n" +
                "                char[] p = System.console().readPassword();\n" +
                "                " + varName + " = new String(p);\n" +
                "            } else {\n" +
                "                // Fallback untuk IDE (tetap terlihat)\n" +
                "                " + varName + " = scanner.nextLine();\n" +
                "            }";
        } else {
            // Jika Bukan Password: Pakai Scanner biasa
            inputMethodCode = "            " + varName + " = scanner.nextLine();";
        }

        // --- Isi Template Block ---
        String block = templates.get("INPUT_BLOCK")
                .replace("%FIELD_LABEL%", labelName)
                .replace("%VAR_NAME%", varName)
                .replace("%INPUT_METHOD%", inputMethodCode)
                .replace("%VALIDATIONS%", validations.toString());

        inputLogic.append("        String ").append(varName).append(";\n"); // Definisi variable di atas loop
        inputLogic.append(block);
    }

    @Override
    public void exitFormulir(ValidFormParser.FormulirContext ctx) {
        // --- Final Assembly menggunakan Template Utama ---
        String code = templates.get("CLASS_SKELETON")
                .replace("%CLASS_NAME%", className)
                .replace("%VARIABLES%", "") // Variable sudah didefinisikan per blok di logika saya di atas, atau bisa dipisah
                .replace("%LOGIC%", inputLogic.toString());

        try (FileWriter writer = new FileWriter(className + ".java")) {
            writer.write(code);
            System.out.println("[GENERATOR] Berhasil membuat aplikasi: " + className + ".java");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}