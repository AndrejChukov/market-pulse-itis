package academy.backend.market_pulse.repository.store;

import academy.backend.market_pulse.repository.store.impl.QuoteFileStoreSkeleton;

public class QuoteFileStoreContractTestImpl extends QuoteFileStoreContractTest {
    @Override
    protected QuoteFileStore createStore() {
        return new QuoteFileStoreSkeleton();
    }
}
