package org.apereo.cas.services;

import org.apereo.cas.authentication.principal.Service;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import static java.util.stream.Collectors.toSet;

/**
 * Default implementation of the {@link ServicesManager} interface.
 *
 * @author Scott Battaglia
 * @since 3.1
 */
public class DefaultServicesManager extends AbstractServicesManager {

    private final Set<RegisteredService> orderedServices = new ConcurrentSkipListSet<>();

    public DefaultServicesManager(final ServiceRegistry serviceRegistry,
                                  final ApplicationEventPublisher eventPublisher,
                                  final Set<String> environments) {
        super(serviceRegistry, eventPublisher, environments);
    }

    @Override
    protected Collection<RegisteredService> getCandidateServicesToMatch(final String serviceId) {
        return this.orderedServices.stream()
                .filter(r -> r.matches(serviceId))
                .filter(this::supports)
                .collect(toSet());
    }

    @Override
    protected void deleteInternal(final RegisteredService service) {
        this.orderedServices.remove(service);
    }

    @Override
    protected void saveInternal(final RegisteredService service) {
        this.orderedServices.clear();
        this.orderedServices.addAll(getAllServices());
    }

    @Override
    protected void loadInternal() {
        this.orderedServices.clear();
        this.orderedServices.addAll(getAllServices());
    }

    @Override
    public boolean supports(final Service service) {
        return service != null
                && !service.getClass().getCanonicalName().equals("org.apereo.cas.support.saml.authentication.principal.SamlService");

    }

    @Override
    public boolean supports(final RegisteredService service) {
        return service != null
                && service.getClass().getCanonicalName().equals(RegexRegisteredService.class.getCanonicalName());
    }

    @Override
    public boolean supports(final Class clazz) {
        return clazz != null && clazz.getCanonicalName().equals(RegexRegisteredService.class.getCanonicalName());
    }
}
