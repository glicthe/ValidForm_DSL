import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class ValidFormCompiler {

    public static void compile(String sourceFile) {
        try {
            File f = new File(sourceFile);
            if (!f.exists()) {
                System.err.println("File DSL tidak ditemukan: " + sourceFile);
                return;
            }

            // 1. Setup ANTLR (Lexer & Parser Baru)
            CharStream input = CharStreams.fromPath(Paths.get(sourceFile));
            ValidFormLexer lexer = new ValidFormLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            ValidFormParser parser = new ValidFormParser(tokens);

            // 2. Parse (Mulai dari rule 'fileDsl')
            ParseTree tree = parser.fileDsl();

            // 3. Generate Code menggunakan CodeGeneratorListener (Template Engine)
            ParseTreeWalker walker = new ParseTreeWalker();
            CodeGeneratorListener generator = new CodeGeneratorListener();
            walker.walk(generator, tree);

        } catch (Exception e) {
            System.out.println("Terjadi kesalahan saat kompilasi:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Jika tidak ada argumen, gunakan default untuk testing
        String fileToCompile = args.length > 0 ? args[0] : "pendaftaran_baru.dsl";
        System.out.println("Memproses file: " + fileToCompile);
        compile(fileToCompile);
    }
}