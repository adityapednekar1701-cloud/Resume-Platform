package com.Application.PlatForm.Service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ResumeTextExtractor {

	public String extractText(byte[] pdfBytes) throws IOException {
		try (PDDocument document = Loader.loadPDF(pdfBytes)) {
			PDFTextStripper stripper = new PDFTextStripper();
			return stripper.getText(document);
		}
	}
}