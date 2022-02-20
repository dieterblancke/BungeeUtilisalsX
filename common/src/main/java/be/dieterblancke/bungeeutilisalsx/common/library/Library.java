package be.dieterblancke.bungeeutilisalsx.common.library;

import be.dieterblancke.bungeeutilisalsx.common.api.utils.reflection.LibraryClassLoader;
import lombok.Data;
import me.lucko.jarrelocator.JarRelocator;
import me.lucko.jarrelocator.Relocation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

@Data
public class Library
{

    private final static List<Library> registry = new ArrayList<>();

    private final String name;
    private final String className;
    private final String downloadURL;
    private final String version;
    private final boolean toLoad;
    private final List<Relocation> relocations;

    public Library( final String name,
                    final String className,
                    final String downloadURL,
                    final String version,
                    final boolean toLoad )
    {
        this( name, className, downloadURL, version, toLoad, new ArrayList<>() );
    }

    public Library( final String name,
                    final String className,
                    final String downloadURL,
                    final String version,
                    final boolean toLoad,
                    final List<Relocation> relocations )
    {
        this.name = name;
        this.className = className;
        this.downloadURL = downloadURL.replace( "{version}", version );
        this.version = version;
        this.toLoad = toLoad;
        this.relocations = relocations;

        registry.add( this );
    }

    public static Library getLibrary( final String name )
    {
        return registry.stream().filter( library -> library.getName().equalsIgnoreCase( name ) ).findFirst().orElse( null );
    }

    public boolean isPresent()
    {
        return this.classFound( className );
    }

    public void load( final File dataFolder, final LibraryClassLoader libraryClassLoader, final Logger logger )
    {
        if ( isPresent() )
        {
            return;
        }
        final File librariesFolder = new File( dataFolder, "libraries" );
        final File originalFolder = new File( librariesFolder, "original" );
        final File relocatedFolder = new File( librariesFolder, "relocated" );

        if ( !originalFolder.exists() )
        {
            originalFolder.mkdirs();
        }
        if ( !relocatedFolder.exists() )
        {
            relocatedFolder.mkdirs();
        }

        final File originalFile = this.ensureOriginalLibraryPresense( originalFolder, logger );
        final File relocatedFile = this.ensureRelocatedLibraryPresense( originalFile, relocatedFolder, logger );

        libraryClassLoader.loadJar( relocatedFile );
        logger.info( "Loaded " + name + " libary!" );
    }

    private File ensureOriginalLibraryPresense( final File originalFolder, final Logger logger )
    {
        final File originalFile = new File( originalFolder, String.format( "%s-v%s-original.jar", name.toLowerCase(), version ) );

        if ( !originalFile.exists() )
        {
            logger.info( "Downloading libary for " + this );

            try ( final InputStream input = new URL( downloadURL ).openStream();
                  final ReadableByteChannel channel = Channels.newChannel( input );
                  final FileOutputStream output = new FileOutputStream( originalFile ) )
            {
                output.getChannel().transferFrom( channel, 0, Long.MAX_VALUE );
                logger.info( "Successfully downloaded libary for " + this );

                final Collection<File> outdatedFiles = getOutdatedFiles( originalFolder, "original" );

                if ( !outdatedFiles.isEmpty() )
                {
                    logger.info( "Removing older original versions of " + this );
                    outdatedFiles.forEach( File::delete );
                    logger.info( "Successfully removed older original versions of " + this );
                }
            }
            catch ( IOException e )
            {
                throw new RuntimeException( "Failed downloading library for " + toString().toLowerCase(), e );
            }
        }
        return originalFile;
    }

    private File ensureRelocatedLibraryPresense( final File originalFile, final File relocatedFolder, final Logger logger )
    {
        final File relocatedFile = new File( relocatedFolder, String.format( "%s-v%s-relocated.jar", name.toLowerCase(), version ) );

        if ( relocatedFile.exists() )
        {
            return relocatedFile;
        }

        final Collection<File> outdatedFiles = getOutdatedFiles( relocatedFolder, "relocated" );

        if ( !outdatedFiles.isEmpty() )
        {
            logger.info( "Removing older relocated versions of " + this );
            outdatedFiles.forEach( File::delete );
            logger.info( "Successfully removed older relocated versions of " + this );
        }

        final JarRelocator relocator = new JarRelocator( originalFile, relocatedFile, this.relocations );

        try
        {
            logger.info( "Relocating library for " + this );
            relocator.run();
            return relocatedFile;
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Unable to relocate library " + toString().toLowerCase(), e );
        }
    }

    private Collection<File> getOutdatedFiles( final File folder, final String type )
    {
        final List<File> outdatedFiles = new ArrayList<>();
        final String name = toString().toLowerCase();

        for ( File library : folder.listFiles() )
        {
            final String jarName = library.getName();

            if ( jarName.startsWith( name ) && !jarName.equals( String.format( "%s-v%s-%s.jar", name.toLowerCase(), version, type ) ) )
            {
                outdatedFiles.add( library );
            }
        }

        return outdatedFiles;
    }

    private boolean classFound( final String clazz )
    {
        try
        {
            Class.forName( clazz );
            return true;
        }
        catch ( ClassNotFoundException e )
        {
            return false;
        }
    }

    @Override
    public String toString()
    {
        return name;
    }
}