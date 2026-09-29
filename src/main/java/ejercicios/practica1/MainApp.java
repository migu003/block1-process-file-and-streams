package ejercicios.practica1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@SuppressWarnings("java:S106")
public class MainApp {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Error: Debes proporcionar la ruta del archivo CSV en args[0].");
            return;
        }

        Path filePath = Paths.get(args[0]);
        List<Person> validPersons = new ArrayList<>();
        AtomicInteger lineNumber = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        processFile(filePath, validPersons, lineNumber, errorCount);
        printSummary(validPersons.size(), errorCount.get());
    }

    private static void processFile(Path filePath, List<Person> validPersons, AtomicInteger lineNumber, AtomicInteger errorCount) {
        try (Stream<String> lines = Files.lines(filePath)) {
            lines.forEach(line -> processSingleLine(line, lineNumber.incrementAndGet(), validPersons, errorCount));
        } catch (IOException e) {
            System.err.println("Error de lectura en el archivo: " + e.getMessage());
        }
    }

    private static void processSingleLine(String line, int currentLine, List<Person> validPersons, AtomicInteger errorCount) {
        try {
            Person person = parseLine(line, currentLine);
            validPersons.add(person);
        } catch (InvalidLineFormatException e) {
            errorCount.incrementAndGet();
            System.err.println("Error en linea " + e.getLineNumber() + " \"" + line + "\" - " + e.getMessage());
        }
    }

    public static Person parseLine(String line, int lineNumber) throws InvalidLineFormatException {
        String[] parts = validateAndSplit(line, lineNumber);
        String name = extractName(parts[0], lineNumber);
        String town = extractTown(parts[1]);
        int age = extractAge(parts[2], lineNumber);

        return new Person(name, town, age);
    }

    private static String[] validateAndSplit(String line, int lineNumber) throws InvalidLineFormatException {
        if (line == null || line.isBlank()) {
            throw new InvalidLineFormatException(lineNumber, "La linea esta vacia");
        }

        String[] parts = line.split(":", -1);

        if (parts.length == 1) {
            throw new InvalidLineFormatException(lineNumber, "Solo un campo");
        }

        if (parts.length == 2) {
            throw new InvalidLineFormatException(lineNumber, "Falta el tercer campo (delimitador final)");
        }

        if (parts.length > 3) {
            throw new InvalidLineFormatException(lineNumber, "La linea tiene mas de 3 campos");
        }

        return parts;
    }

    private static String extractName(String rawName, int lineNumber) throws InvalidLineFormatException {
        String name = rawName.trim();
        if (name.isEmpty()) {
            throw new InvalidLineFormatException(lineNumber, "Campo 'name' vacio");
        }
        return name;
    }

    private static String extractTown(String rawTown) {
        String town = rawTown.trim();
        return town.isEmpty() ? "unknown" : town;
    }

    private static int extractAge(String rawAge, int lineNumber) throws InvalidLineFormatException {
        String cleanAge = rawAge.trim();
        if (cleanAge.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(cleanAge);
        } catch (NumberFormatException e) {
            throw new InvalidLineFormatException(lineNumber, "El campo 'age' no es un numero entero valido");
        }
    }

    private static void printSummary(int validCount, int errorCount) {
        System.out.println("Total de personas cargadas correctamente: " + validCount);
        System.out.println("Total de lineas incorrectas: " + errorCount);
    }
}