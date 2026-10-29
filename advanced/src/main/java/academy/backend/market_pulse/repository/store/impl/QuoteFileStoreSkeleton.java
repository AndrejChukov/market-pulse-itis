package academy.backend.market_pulse.repository.store.impl;

import academy.backend.market_pulse.factory.InstrumentFactories;
import academy.backend.market_pulse.model.Currency;
import academy.backend.market_pulse.model.Instrument;
import academy.backend.market_pulse.model.Quote;
import academy.backend.market_pulse.model.Stock;
import academy.backend.market_pulse.repository.store.QuoteFileStore;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Заготовка для самостоятельной работы (см. «Требования к файловому хранилищу.md», семинар 7) —
 * структура есть, решения нет. Формат файла — на ваш выбор (JSON, CSV, построчный текст); главное,
 * чтобы {@code load(save(quotes))} возвращал те же котировки. Проверьте себя тестом
 * {@code QuoteFileStoreContractTest} (замените в нём эталонную реализацию на свою).
 */
public class QuoteFileStoreSkeleton implements QuoteFileStore {

    @Override
    public void save(Path path, List<Quote> quotes) {
        try (BufferedWriter br = Files.newBufferedWriter(path)) {
            for (Quote quote : quotes) {
                String output = quote.getInstrument().getTicker() + "," +
                        quote.getInstrument().getType() + "," +
                        quote.getInstrument().getCurrency() + "," +
                        quote.getInstrument().getName() + "," +
                        quote.getPrice() + "," +
                        quote.getChangePercent();
                br.write(output);
                br.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Quote> load(Path path) {
        try {
            return Files.readAllLines(path).stream()
                    .map(line -> {
                        String[] parts = line.split(",");
                        return new Quote(
                                InstrumentFactories.create(parts[1], parts[0], parts[3], Currency.valueOf(parts[2])),
                                new BigDecimal(parts[4].trim()),
                                new BigDecimal(parts[5].trim())
                        );
                    })
                    .collect(Collectors.toList());
        } catch (NoSuchFileException e) {
            return List.of();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}