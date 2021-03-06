import { Component, OnInit, Input, OnChanges, SimpleChanges, OnDestroy } from '@angular/core';
import { SidebarService } from './sidebar.service';
import { CategoryService, ProductService } from 'app/shared';
import { HttpResponse } from '@angular/common/http';
import { Category } from 'app/shared/model/category.model';
import { Criteria } from 'app/shared/model/searchCriteria';
import { Options, LabelType } from 'ng5-slider';
import { CriteriaService } from 'app/shared/service/criteria.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
    selector: 'jhi-sidebar',
    templateUrl: './sidebar.component.html',
    styleUrls: ['sidebar.component.scss']
})
export class SidebarComponent implements OnInit, OnDestroy {
    allCategories: Category[];
    incomingCat = [];
    allSelectedCategory: string[] = [];
    min = 0;
    max = 1000;
    options: Options = {
        floor: 0,
        ceil: 10000,
        step: 50,
        translate: (value: number, label: LabelType): string => {
            switch (label) {
                case LabelType.Low:
                    return '' + value;
                case LabelType.High:
                    return '' + value;
                default:
                    return value + '€';
            }
        }
    };

    constructor(
        private criteriaService: CriteriaService,
        private categoryService: CategoryService,
        private productService: ProductService,
        private mySnackBar: MatSnackBar
    ) {}

    ngOnInit() {
        this.categoryService.query().subscribe((res: HttpResponse<Category[]>) => this.bindBody(res.body));
        this.criteriaService.getCriteria().subscribe(value => this.bindCategory(value));
    }

    ngOnDestroy() {}

    private bindBody(data: Category[]) {
        this.allCategories = data;
    }

    private bindCategory(value: Criteria[]) {
        const allSelectedCat = this.criteriaService.getSpecificCriteria(value, 'category');
        if (allSelectedCat !== null) {
            this.incomingCat = allSelectedCat.map(a => a.value);
            this.allSelectedCategory = this.incomingCat;
        }
    }

    public onCheckboxChange(categoryIdCheckbox: string, event: any) {
        if (event.target.checked) {
            this.allSelectedCategory.push(categoryIdCheckbox);
            this.criteriaService.addCriteria('category', categoryIdCheckbox);
        } else {
            for (let i = 0; i < this.allSelectedCategory.length; i++) {
                if (this.allSelectedCategory[i] === categoryIdCheckbox) {
                    this.allSelectedCategory.splice(i, 1);
                }
            }
            this.criteriaService.deleteCriteria('category', categoryIdCheckbox);
        }
    }

    public applyPriceRange() {
        if (this.criteriaService.checkIfCriteriaExist('price')) {
            this.criteriaService.updateCriteria('price', this.min + '|' + this.max);
        } else {
            this.criteriaService.addCriteria('price', this.min + '|' + this.max);
        }
        this.mySnackBar.open('Price criteria added for the product search !', null, {
            duration: 2500,
            verticalPosition: 'bottom',
            horizontalPosition: 'end'
        });
    }

    public removePriceRange() {
        this.criteriaService.deleteAllCriteriaType('price');
        this.mySnackBar.open('Price criteria removed for the product search !', null, {
            duration: 2500,
            verticalPosition: 'bottom',
            horizontalPosition: 'end'
        });
    }
}
