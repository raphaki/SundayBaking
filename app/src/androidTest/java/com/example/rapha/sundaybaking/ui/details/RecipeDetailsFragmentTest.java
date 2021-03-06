package com.example.rapha.sundaybaking.ui.details;

import android.arch.lifecycle.MutableLiveData;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.rapha.sundaybaking.R;
import com.example.rapha.sundaybaking.SingleFragmentTestingActivity;
import com.example.rapha.sundaybaking.data.models.Ingredient;
import com.example.rapha.sundaybaking.data.models.InstructionStep;
import com.example.rapha.sundaybaking.util.DataUtil;
import com.example.rapha.sundaybaking.util.EspressoTestUtil;
import com.example.rapha.sundaybaking.util.StringUtil;
import com.example.rapha.sundaybaking.util.ViewModelUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class RecipeDetailsFragmentTest {

    private final String RECIPE_NAME = "Apple pie";
    @Rule
    public final ActivityTestRule<SingleFragmentTestingActivity> activityTestRule =
            new ActivityTestRule<>(SingleFragmentTestingActivity.class);
    @Mock
    private RecipeDetailsViewModel viewModel;
    private final MutableLiveData<List<Ingredient>> ingredients = new MutableLiveData<>();
    private final MutableLiveData<List<InstructionStep>> steps = new MutableLiveData<>();
    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(viewModel.getIngredients()).thenReturn(ingredients);
        when(viewModel.getInstructionSteps()).thenReturn(steps);
        EspressoTestUtil.disableAnimations(activityTestRule);

        RecipeDetailsFragment fragment = RecipeDetailsFragment.forRecipe(RECIPE_NAME);
        fragment.viewModelFactory = ViewModelUtil.createFor(viewModel);
        activityTestRule.getActivity().setFragment(fragment);
    }

    @Test
    public void insertIngredients_showIngredientsList() {
        String[] ingredientArray = {
                "Banana", "Banunu", "Banini", "Banono", "Banene",
                "Sugar", "Sugir", "Suger", "Sugor", "Sugur"};
        List<Ingredient> ingredientsList = DataUtil.createIngredients(ingredientArray);
        ingredients.postValue(ingredientsList);
        for (Ingredient ingredient : ingredientsList) {
            onView(withText(ingredient.getIngredient())).check(matches(isDisplayed()));
        }
    }

    @Test
    public void insertDirections_showsDirectionsSteps() {
        String[] shortDescriptions = {"Wake up", "Drink coffee", "Brush teeth", "Drink coffee",
                "Go to work"};
        List<InstructionStep> shortDescriptionsList = DataUtil.createDirectionSteps(shortDescriptions);
        steps.postValue(shortDescriptionsList);
        for (InstructionStep step : shortDescriptionsList) {
//            onView(withId(R.id.details_scrollview)).perform(ViewActions.swipeUp());
//            onView(withText(StringUtil.prepareShortDescription(step))).perform(scrollTo());
//            onView(withContentDescription(R.string.recipe_rv_cd)).perform(RecyclerViewActions.scrollToPosition(step.getStepNo()));
            onView(withText(StringUtil.prepareShortDescription(step))).check(matches(isDisplayed()));
        }
    }

    @Test
    public void createFragment_callsChangesRecipeInViewModelWithCorrectName() {
        // wait for UI
        onView(withId(R.id.details_upper_layout)).check(matches(isDisplayed()));
        verify(viewModel).changeCurrentRecipe(stringCaptor.capture());
        assertEquals(stringCaptor.getValue(), RECIPE_NAME);
    }
}