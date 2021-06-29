import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddRuleDialogueComponent } from './add-rule-dialogue.component';

describe('AddRuleDialogueComponent', () => {
  let component: AddRuleDialogueComponent;
  let fixture: ComponentFixture<AddRuleDialogueComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddRuleDialogueComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddRuleDialogueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
