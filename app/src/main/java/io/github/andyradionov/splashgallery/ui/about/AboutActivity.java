package io.github.andyradionov.splashgallery.ui.about;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import io.github.andyradionov.splashgallery.R;
import io.github.andyradionov.splashgallery.ui.base.BaseActivity;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

/**
 * About Screen
 * @author Andrey Radionov
 */
public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActionBar(getString(R.string.about_title));

        final String description = getString(R.string.about_description);
        final String connectTitle = getString(R.string.connect_with_us_title);

        final View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription(description)
                .setImage(R.drawable.logo_about)
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup(connectTitle)
                .addEmail("not.exist@mail.com")
                .addWebsite("http://andyradionov.github.io/")
                .addFacebook("andy.radionov")
                .addGitHub("andyradionov")
                .addItem(getCopyRightsElement())
                .create();

        setContentView(aboutPage);
    }

    private Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = "Andrey Radionov 2018";
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.ic_copyright_black);
        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.CENTER);
        return copyRightsElement;
    }
}
