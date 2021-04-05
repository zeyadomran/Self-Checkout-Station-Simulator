package org.lsmr.selfcheckout.software;

import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CardReaderListener;

public class CardReaderListenerStub implements CardReaderListener{
    private boolean isDisabled = false;
    private boolean cardInReader = false;
    private CardData latestCard = null;

    @Override
    public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) { this.isDisabled = false; }

    @Override
    public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) { this.isDisabled = true; }

    @Override
    public void cardInserted(CardReader reader) { this.cardInReader = true; }

    @Override
    public void cardRemoved(CardReader reader) { this.cardInReader = false; }

    @Override
    public void cardTapped(CardReader reader) {}

    @Override
    public void cardSwiped(CardReader reader) {}

    @Override
    public void cardDataRead(CardReader reader, CardData data) {
        this.latestCard = data;
    }

    /**
     * Get whether the card reader is disabled.
     * 
     * @return Wether the card reader is disabled.
     */
    public boolean getIsDisabled() { return this.isDisabled; }

    /**
     * Get whether there is a card in the card reader.
     * 
     * @return Whether there is a card in the card reader.
     */
    public boolean getIsCardInReader() { return this.cardInReader; }

    /**
     * Get the latest card inserted/swiped/tapped.
     * 
     * @return The latest card inserted/swiped/tapped.
     */
    public CardData getLatestCard() { return this.latestCard; }
    
}
