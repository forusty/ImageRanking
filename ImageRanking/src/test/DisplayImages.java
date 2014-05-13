package test;

import java.util.List;


import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class DisplayImages {

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			DisplayImages window = new DisplayImages();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 * @param initialRanking 
	 */
	public void open()
	{
		Display display = Display.getDefault();
		Shell shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(shell, SWT.NONE);

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	public void open(List<test.Image> results,String message) {
		Display display = Display.getDefault();
		Shell shell = new Shell();
		shell.setSize(450, 300);
		shell.setText(message);
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		final ScrolledComposite scrollComposite = new ScrolledComposite(shell, SWT.V_SCROLL | SWT.BORDER);
		final Composite parent = new Composite(scrollComposite, SWT.NONE);
		int size=0;
		if(results.size()>100)
		{
			size=100;
		}
		else
		{
			size=results.size();
		}
		for(int i=0;i<size;i++)
		{
			test.Image temp=results.get(i);
			Image image=new Image(display,temp.getUrl());
			ImageData data = image.getImageData();
			data=data.scaledTo(150, 150);
			Image image2=new Image(display,data);
			Label label = new Label(parent, SWT.NONE);
			label.setImage(image2);
			//image.dispose();
			//image2.dispose();
		}	
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.wrap = true;
		parent.setLayout(layout);
		scrollComposite.setContent(parent);
		scrollComposite.setExpandVertical(true);
		scrollComposite.setExpandHorizontal(true);
		scrollComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				Rectangle r = scrollComposite.getClientArea();
				scrollComposite.setMinSize(parent.computeSize(r.width, SWT.DEFAULT));
			}
		});
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
