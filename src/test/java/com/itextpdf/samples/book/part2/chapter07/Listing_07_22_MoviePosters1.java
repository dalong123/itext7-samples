package com.itextpdf.samples.book.part2.chapter07;

import com.itextpdf.basics.geom.PageSize;
import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.basics.image.ImageFactory;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;
import com.itextpdf.core.testutils.annotations.type.SampleTest;
import com.itextpdf.model.Canvas;
import com.itextpdf.model.Document;
import com.itextpdf.model.element.Image;
import com.itextpdf.samples.GenericTest;
import com.lowagie.database.DatabaseConnection;
import com.lowagie.database.HsqldbConnection;
import com.lowagie.filmfestival.Movie;
import com.lowagie.filmfestival.PojoFactory;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.experimental.categories.Category;

@Category(SampleTest.class)
public class Listing_07_22_MoviePosters1 extends GenericTest {
    public static final String DEST
            = "./target/test/resources/book/part2/chapter07/Listing_07_22_MoviePosters1.pdf";
    /**
     * Pattern for an info String.
     */
    public static final String INFO = "Movie produced in %s; run length: %s";
    /**
     * Path to IMDB.
     */
    public static final String IMDB
            = "http://imdb.com/title/tt%s/";
    public static final String RESOURCE = "./src/test/resources/book/part2/chapter07/posters/%s.jpg";

    public static void main(String args[]) throws IOException, SQLException {
        new Listing_07_22_MoviePosters1().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        // Create a database connection
        DatabaseConnection connection = new HsqldbConnection("filmfestival");

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(DEST));
        Document doc = new Document(pdfDoc, new PageSize(PageSize.A4));
        // Add the movie posters
        Image img;
        PdfLinkAnnotation linkAnnotation;
        // Annotation annotation;
        float x = 11.5f;
        float y = 769.7f;
        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(595, 84.2f));
        PdfCanvas xObjectCanvas = new PdfCanvas(xObject, pdfDoc);
        xObjectCanvas.rectangle(8, 8, 571, 60);
        for (float f = 8.25f; f < 581; f += 6.5f) {
            xObjectCanvas.roundRectangle(f, 8.5f, 6, 3, 1.5f);
            xObjectCanvas.roundRectangle(f, 72.5f, 6, 3, 1.5f);
        }
        xObjectCanvas.setFillColorGray(0.1f);
        xObjectCanvas.eoFill();

        PdfCanvas pdfCanvas = new PdfCanvas(pdfDoc.addNewPage());
        for (int i = 0; i < 10; i++) {
            pdfCanvas.addXObject(xObject, 0, i * 84.2f);
        }
        Canvas canvas = new Canvas(pdfCanvas, pdfDoc, pdfDoc.getLastPage().getPageSize());
        for (Movie movie : PojoFactory.getMovies(connection)) {
            img = new Image(ImageFactory.getImage(String.format(RESOURCE, movie.getImdb())));
            img.scaleToFit(1000, 60);
            img.setFixedPosition(x + (45 - img.getImageScaledWidth()) / 2, y);
            linkAnnotation = new PdfLinkAnnotation(pdfDoc, new Rectangle(x + (45 - img.getImageScaledWidth()) / 2, y,
                    img.getImageScaledWidth(), img.getImageScaledHeight()));
            linkAnnotation.setAction(PdfAction.createURI(pdfDoc, String.format(IMDB, movie.getImdb())));
            pdfDoc.getLastPage().addAnnotation(linkAnnotation);
            canvas.add(img);
            x += 48;
            if (x > 578) {
                x = 11.5f;
                y -= 84.2f;
            }
        }
        doc.close();
        // Close the database connection
        connection.close();
    }
}