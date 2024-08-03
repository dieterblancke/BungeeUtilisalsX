package dev.endoy.bungeeutilisalsx.common.api.utils;

import dev.endoy.bungeeutilisalsx.common.api.utils.config.configs.WebhookConfig.DiscordWebhookConfig;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.HasMessagePlaceholders;
import dev.endoy.configuration.api.ISection;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.List;
import java.util.*;

/**
 * Class used to execute Discord Webhooks with low effort
 */
public class DiscordWebhook
{

    private final String url;
    private final List<EmbedObject> embeds = new ArrayList<>();
    private String content;
    private String username;
    private String avatarUrl;
    private boolean tts;

    /**
     * Constructs a new DiscordWebhook instance
     *
     * @param url The webhook URL obtained in Discord
     */
    public DiscordWebhook( final String url )
    {
        this.url = url;
    }

    public DiscordWebhook( final DiscordWebhookConfig config )
    {
        this( config.url() );
        this.setUsername( config.userName() );
        this.setAvatarUrl( config.avatarUrl() );
    }

    public void setContent( String content )
    {
        this.content = content;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public void setAvatarUrl( String avatarUrl )
    {
        this.avatarUrl = avatarUrl;
    }

    public void setTts( boolean tts )
    {
        this.tts = tts;
    }

    public void addEmbed( EmbedObject embed )
    {
        this.embeds.add( embed );
    }

    public void execute() throws IOException
    {
        if ( this.content == null && this.embeds.isEmpty() )
        {
            throw new IllegalArgumentException( "Set content or add at least one EmbedObject" );
        }

        JSONObject json = new JSONObject();

        json.put( "content", this.content );
        json.put( "username", this.username );
        json.put( "avatar_url", this.avatarUrl );
        json.put( "tts", this.tts );

        if ( !this.embeds.isEmpty() )
        {
            List<JSONObject> embedObjects = new ArrayList<>();

            for ( EmbedObject embed : this.embeds )
            {
                JSONObject jsonEmbed = new JSONObject();

                jsonEmbed.put( "title", embed.getTitle() );
                jsonEmbed.put( "description", embed.getDescription() );
                jsonEmbed.put( "url", embed.getUrl() );

                if ( embed.getColor() != null )
                {
                    Color color = embed.getColor();
                    int rgb = color.getRed();
                    rgb = ( rgb << 8 ) + color.getGreen();
                    rgb = ( rgb << 8 ) + color.getBlue();

                    jsonEmbed.put( "color", rgb );
                }

                EmbedObject.Footer footer = embed.getFooter();
                EmbedObject.Image image = embed.getImage();
                EmbedObject.Thumbnail thumbnail = embed.getThumbnail();
                EmbedObject.Author author = embed.getAuthor();
                List<EmbedObject.Field> fields = embed.getFields();

                if ( footer != null )
                {
                    JSONObject jsonFooter = new JSONObject();

                    jsonFooter.put( "text", footer.text() );
                    jsonFooter.put( "icon_url", footer.iconUrl() );
                    jsonEmbed.put( "footer", jsonFooter );
                }

                if ( image != null )
                {
                    JSONObject jsonImage = new JSONObject();

                    jsonImage.put( "url", image.url() );
                    jsonEmbed.put( "image", jsonImage );
                }

                if ( thumbnail != null )
                {
                    JSONObject jsonThumbnail = new JSONObject();

                    jsonThumbnail.put( "url", thumbnail.url() );
                    jsonEmbed.put( "thumbnail", jsonThumbnail );
                }

                if ( author != null )
                {
                    JSONObject jsonAuthor = new JSONObject();

                    jsonAuthor.put( "name", author.name() );
                    jsonAuthor.put( "url", author.url() );
                    jsonAuthor.put( "icon_url", author.iconUrl() );
                    jsonEmbed.put( "author", jsonAuthor );
                }

                List<JSONObject> jsonFields = new ArrayList<>();
                for ( EmbedObject.Field field : fields )
                {
                    JSONObject jsonField = new JSONObject();

                    jsonField.put( "name", field.name() );
                    jsonField.put( "value", field.value() );
                    jsonField.put( "inline", field.inline() );

                    jsonFields.add( jsonField );
                }

                jsonEmbed.put( "fields", jsonFields.toArray() );
                embedObjects.add( jsonEmbed );
            }

            json.put( "embeds", embedObjects.toArray() );
        }

        URL url = new URL( this.url );
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.addRequestProperty( "Content-Type", "application/json" );
        connection.addRequestProperty( "User-Agent", "Java-DiscordWebhook-BY-Gelox_" );
        connection.setDoOutput( true );
        connection.setRequestMethod( "POST" );

        OutputStream stream = connection.getOutputStream();
        stream.write( json.toString().getBytes() );
        stream.flush();
        stream.close();

        connection.getInputStream().close(); // I'm not sure why but it doesn't work without getting the InputStream
        connection.disconnect();
    }

    public static class EmbedObject
    {

        private final List<Field> fields = new ArrayList<>();
        private String title;
        private String description;
        private String url;
        private Color color;
        private Footer footer;
        private Thumbnail thumbnail;
        private Image image;
        private Author author;

        public static EmbedObject fromSection( final ISection section, final HasMessagePlaceholders placeholders )
        {
            final EmbedObject embedObject = new EmbedObject();

            if ( section.exists( "description" ) )
            {
                embedObject.setDescription(
                    Utils.replacePlaceHolders( section.getString( "description" ), placeholders )
                );
            }
            if ( section.exists( "image" ) )
            {
                embedObject.setImage(
                    Utils.replacePlaceHolders( section.getString( "image" ), placeholders )
                );
            }
            if ( section.exists( "thumbnail" ) )
            {
                embedObject.setThumbnail(
                    Utils.replacePlaceHolders( section.getString( "thumbnail" ), placeholders )
                );
            }
            if ( section.exists( "title" ) )
            {
                embedObject.setTitle(
                    Utils.replacePlaceHolders( section.getString( "title" ), placeholders )
                );
            }
            if ( section.exists( "url" ) )
            {
                embedObject.setUrl(
                    Utils.replacePlaceHolders( section.getString( "url" ), placeholders )
                );
            }
            if ( section.exists( "color" ) )
            {
                embedObject.setColor( new Color( section.getInteger( "color" ) ) );
            }
            if ( section.exists( "author" ) )
            {
                embedObject.setAuthor(
                    Utils.replacePlaceHolders( section.getString( "author.name" ), placeholders ),
                    Utils.replacePlaceHolders( section.getString( "author.url" ), placeholders ),
                    Utils.replacePlaceHolders( section.getString( "author.icon" ), placeholders )
                );
            }
            if ( section.exists( "footer" ) )
            {
                embedObject.setFooter(
                    Utils.replacePlaceHolders( section.getString( "footer.text" ), placeholders ),
                    Utils.replacePlaceHolders( section.getString( "footer.icon" ), placeholders )
                );
            }

            for ( ISection field : section.getSectionList( "fields" ) )
            {
                embedObject.addField(
                    Utils.replacePlaceHolders( field.getString( "name" ), placeholders ),
                    Utils.replacePlaceHolders( field.getString( "value" ), placeholders ),
                    field.getBoolean( "inline" )
                );
            }

            return embedObject;
        }

        public String getTitle()
        {
            return title;
        }

        public EmbedObject setTitle( String title )
        {
            this.title = title;
            return this;
        }

        public String getDescription()
        {
            return description;
        }

        public EmbedObject setDescription( String description )
        {
            this.description = description;
            return this;
        }

        public String getUrl()
        {
            return url;
        }

        public EmbedObject setUrl( String url )
        {
            this.url = url;
            return this;
        }

        public Color getColor()
        {
            return color;
        }

        public EmbedObject setColor( Color color )
        {
            this.color = color;
            return this;
        }

        public Footer getFooter()
        {
            return footer;
        }

        public Thumbnail getThumbnail()
        {
            return thumbnail;
        }

        public EmbedObject setThumbnail( String url )
        {
            this.thumbnail = new Thumbnail( url );
            return this;
        }

        public Image getImage()
        {
            return image;
        }

        public EmbedObject setImage( String url )
        {
            this.image = new Image( url );
            return this;
        }

        public Author getAuthor()
        {
            return author;
        }

        public List<Field> getFields()
        {
            return fields;
        }

        public EmbedObject setFooter( String text, String icon )
        {
            this.footer = new Footer( text, icon );
            return this;
        }

        public EmbedObject setAuthor( String name, String url, String icon )
        {
            this.author = new Author( name, url, icon );
            return this;
        }

        public EmbedObject addField( String name, String value, boolean inline )
        {
            this.fields.add( new Field( name, value, inline ) );
            return this;
        }

        private record Footer(String text, String iconUrl)
        {
        }

        private record Thumbnail(String url)
        {
        }

        private record Image(String url)
        {
        }

        private record Author(String name, String url, String iconUrl)
        {
        }

        private record Field(String name, String value, boolean inline)
        {
        }
    }

    private static class JSONObject
    {

        private final HashMap<String, Object> map = new HashMap<>();

        void put( String key, Object value )
        {
            if ( value != null )
            {
                map.put( key, value );
            }
        }

        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            Set<Map.Entry<String, Object>> entrySet = map.entrySet();
            builder.append( "{" );

            int i = 0;
            for ( Map.Entry<String, Object> entry : entrySet )
            {
                Object val = entry.getValue();
                builder.append( quote( entry.getKey() ) ).append( ":" );

                if ( val instanceof String )
                {
                    builder.append( quote( String.valueOf( val ) ) );
                }
                else if ( val instanceof Integer )
                {
                    builder.append( Integer.valueOf( String.valueOf( val ) ) );
                }
                else if ( val instanceof Boolean )
                {
                    builder.append( val );
                }
                else if ( val instanceof JSONObject )
                {
                    builder.append( val );
                }
                else if ( val.getClass().isArray() )
                {
                    builder.append( "[" );
                    int len = Array.getLength( val );
                    for ( int j = 0; j < len; j++ )
                    {
                        builder.append( Array.get( val, j ).toString() ).append( j != len - 1 ? "," : "" );
                    }
                    builder.append( "]" );
                }

                builder.append( ++i == entrySet.size() ? "}" : "," );
            }

            return builder.toString();
        }

        private String quote( String string )
        {
            return "\"" + string + "\"";
        }
    }
}