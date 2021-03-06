import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiLanguageService } from 'ng-jhipster';

import { VERSION } from 'app/app.constants';
import { JhiLanguageHelper, Principal, LoginModalService, LoginService } from 'app/core';
import { ProfileService } from '../profiles/profile.service';
import { HttpResponse } from '@angular/common/http';
import { Category } from 'app/shared/model/category.model';
import { CategoryService } from 'app/shared';
import { SafeResourceUrl } from '@angular/platform-browser';
import { JhiEventManager } from 'ng-jhipster';
import { SidebarService } from '../sidebar/sidebar.service';

@Component({
    selector: 'jhi-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['navbar.scss']
})
export class NavbarComponent implements OnInit {
    mainImage: SafeResourceUrl = 'content/images/shoping_cart.png';
    inProduction: boolean;
    isNavbarCollapsed: boolean;
    languages: any[];
    swaggerEnabled: boolean;
    modalRef: NgbModalRef;
    version: string;
    allCategories: Category[];
    searchField: string;
    itemInBasket = 0;

    constructor(
        private loginService: LoginService,
        private languageService: JhiLanguageService,
        private languageHelper: JhiLanguageHelper,
        private principal: Principal,
        private loginModalService: LoginModalService,
        private profileService: ProfileService,
        private router: Router,
        private categoryService: CategoryService,
        private eventManager: JhiEventManager,
        private sidebarService: SidebarService
    ) {
        this.version = VERSION ? 'v' + VERSION : '';
        this.isNavbarCollapsed = true;
    }

    ngOnInit() {
        this.languageHelper.getAll().then(languages => {
            this.languages = languages;
        });

        this.profileService.getProfileInfo().then(profileInfo => {
            this.inProduction = profileInfo.inProduction;
            this.swaggerEnabled = profileInfo.swaggerEnabled;
        });

        this.categoryService.query().subscribe((res: HttpResponse<Category[]>) => this.bindBody(res.body));

        this.eventManager.subscribe('cartCountChange', message => {
            this.itemInBasket = parseInt(message.content, 10);
        });
    }

    changeLanguage(languageKey: string) {
        this.languageService.changeLanguage(languageKey);
    }

    collapseNavbar() {
        this.isNavbarCollapsed = true;
    }

    isAuthenticated() {
        return this.principal.isAuthenticated();
    }

    login() {
        this.modalRef = this.loginModalService.open();
    }

    logout() {
        this.collapseNavbar();
        this.loginService.logout();
        this.router.navigate(['']);
    }

    toggleNavbar() {
        this.isNavbarCollapsed = !this.isNavbarCollapsed;
    }

    getImageUrl() {
        return this.isAuthenticated() ? this.principal.getImageUrl() : null;
    }

    private bindBody(data: Category[]) {
        this.allCategories = data;
    }

    navigateSearch() {
        this.router.navigate(['displayProducts', 'search=' + this.searchField]);
    }

    categorySelect(categoryId: string) {
        this.isNavbarCollapsed = true;
        this.router.navigate(['displayProducts', 'category=' + categoryId]);
    }
}
