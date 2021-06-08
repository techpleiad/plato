import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkspaceDialogueComponent } from './workspace-dialogue.component';

describe('WorkspaceDialogueComponent', () => {
  let component: WorkspaceDialogueComponent;
  let fixture: ComponentFixture<WorkspaceDialogueComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WorkspaceDialogueComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkspaceDialogueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
