package be.dieterblancke.bungeeutilisalsx.common.api.utils.other;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class RandomIterator<T> implements Iterator<T>
{

    private static final Random RANDOM = new Random();
    private final List<T> data;
    private final int[] iteratedIndexes;

    public RandomIterator( final List<T> data )
    {
        this.data = data;
        this.iteratedIndexes = new int[data.size()];
    }

    @Override
    public boolean hasNext()
    {
        return hasZeroValue();
    }

    @Override
    public T next()
    {
        final int[] activeIndexes = getActiveIndexes();
        final int idx = activeIndexes[RANDOM.nextInt( activeIndexes.length )];
        iteratedIndexes[idx] = 1;

        return data.get( idx );
    }

    private int[] getActiveIndexes()
    {
        final int[] zeroIndexes = new int[getZeroValueCount()];
        int counter = 0;

        for ( int i = 0; i < iteratedIndexes.length; i++ )
        {
            if ( iteratedIndexes[i] == 0 )
            {
                zeroIndexes[counter] = i;
                counter++;
            }
        }

        return zeroIndexes;
    }

    private int getZeroValueCount()
    {
        int amount = 0;
        for ( int iteratedIndex : iteratedIndexes )
        {
            if ( iteratedIndex == 0 )
            {
                amount++;
            }
        }

        return amount;
    }

    private boolean hasZeroValue()
    {
        for ( int iteratedIndex : iteratedIndexes )
        {
            if ( iteratedIndex == 0 )
            {
                return true;
            }
        }

        return false;
    }
}
