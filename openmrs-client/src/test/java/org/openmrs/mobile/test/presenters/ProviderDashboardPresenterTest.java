package org.openmrs.mobile.test.presenters;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.mobile.activities.providerdashboard.ProviderDashboardContract;
import org.openmrs.mobile.activities.providerdashboard.ProviderDashboardPresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.repository.ProviderRepository;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.dao.ProviderRoomDAO;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.test.ACUnitTestBase;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest({NetworkUtils.class, ToastUtil.class, OpenMRS.class, OpenMRSLogger.class})
public class ProviderDashboardPresenterTest extends ACUnitTestBase {
    @Rule
    public InstantTaskExecutorRule taskExecutorRule = new InstantTaskExecutorRule();
    @Mock
    private RestApi restApi;
    @Mock
    private ProviderDashboardContract.View providerManagerView;
    @Mock
    private OpenMRSLogger openMRSLogger;
    @Mock
    private OpenMRS openMRS;
    private ProviderDashboardPresenter providerDashboardPresenter;
    private ProviderRepository providerRepository;

    @Before
    public void setup() {
        this.providerRepository = new ProviderRepository(openMRS, restApi, openMRSLogger);
        ProviderRoomDAO providerRoomDao = Mockito.mock(ProviderRoomDAO.class);
        ProviderRoomDAO spyProviderRoomDao = spy(providerRoomDao);
        doNothing().when(spyProviderRoomDao).updateProviderByUuid(Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.anyString(), Mockito.anyString());

        this.providerRepository.setProviderRoomDao(spyProviderRoomDao);
        this.providerDashboardPresenter = new ProviderDashboardPresenter(providerManagerView, restApi, providerRepository);
        mockStaticMethods();
    }

    @Test
    public void shouldReturnSuccessOnUpdateProvider() {
        Provider provider = createProvider(1l, "doctor");
        Mockito.lenient().when(NetworkUtils.isOnline()).thenReturn(true);
        Mockito.lenient().when(restApi.UpdateProvider(provider.getUuid(), provider)).thenReturn(mockSuccessCall(provider));

        providerDashboardPresenter.updateProvider(provider);
        Mockito.verify(providerManagerView).setupBackdrop(provider);
    }

    private void mockStaticMethods() {
        PowerMockito.mockStatic(NetworkUtils.class);
        PowerMockito.mockStatic(OpenMRS.class);
        PowerMockito.mockStatic(OpenMRSLogger.class);
        Mockito.lenient().when(OpenMRS.getInstance()).thenReturn(openMRS);
        PowerMockito.when(openMRS.getOpenMRSLogger()).thenReturn(openMRSLogger);
        PowerMockito.mockStatic(ToastUtil.class);
    }
}
