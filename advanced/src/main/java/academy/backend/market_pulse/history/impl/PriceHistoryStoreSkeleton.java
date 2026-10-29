package academy.backend.market_pulse.history.impl;

import academy.backend.market_pulse.history.PriceHistoryStore;
import academy.backend.market_pulse.history.PricePoint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Заготовка для самостоятельной работы (см. «Требования к файловому хранилищу.md», семинар 7) —
 * структура есть, решения нет. Формат файла — на ваш выбор (бинарный, текстовый и т.д.); главное,
 * чтобы каждый {@link #append} создавал НОВЫЙ файл (не переписывал предыдущий) и
 * {@link #load(Path, String)} возвращал все сохранённые точки по тикеру в порядке возрастания
 * времени. Проверьте себя тестом {@code PriceHistoryStoreContractTest} (замените в нём эталонную
 * реализацию на свою).
 */
public class PriceHistoryStoreSkeleton implements PriceHistoryStore {

    @Override
    public Path append(Path directory, PricePoint point) {
        try {
            Path tickerDir = directory.resolve(point.ticker());
            Files.createDirectories(tickerDir);

            Path filePath = tickerDir.resolve(point.timestamp() + ".txt");
            String content = point.timestamp() + "," + point.price();

            Files.writeString(filePath, content);

            return filePath;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PricePoint> load(Path directory, String ticker) {
        Path tickerDir = directory.resolve(ticker);

        if (Files.notExists(tickerDir)) {
            return List.of();
        }

        try (Stream<Path> filesStream = Files.list(tickerDir)) {
            return filesStream
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .map(this::readPointFromFile)
                    .toList();
        } catch (NoSuchFileException e) {
            return List.of();
        }
        catch (IOException e) {
            throw new RuntimeException("Ошибка чтения истории цен для тикера: " + ticker, e);
        }
    }

    private PricePoint readPointFromFile(Path filePath) {
        try {
            String line = Files.readString(filePath);
            String[] parts = line.split(",");

            Instant timestamp = Instant.parse(parts[0]);
            BigDecimal price = new BigDecimal(parts[1]);

            String ticker = filePath.getParent().getFileName().toString();

            return new PricePoint(ticker, timestamp, price);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
