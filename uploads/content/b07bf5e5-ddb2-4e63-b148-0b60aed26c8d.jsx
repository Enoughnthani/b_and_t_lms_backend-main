import { useState } from 'react';
import { 
  FaPlus, FaEdit, FaTrash, FaSearch, FaFileAlt, 
  FaBook, FaClipboardList, FaCalendarAlt, FaClock,
  FaCheckCircle, FaHourglassHalf, FaEllipsisV,
  FaUpload, FaDownload, FaEye, FaBolt, FaFire,
  FaStar, FaArrowRight, FaLayerGroup, FaTimes,
  FaChevronDown, FaChevronUp
} from 'react-icons/fa';

export default function AssessmentPage() {
  const [assessments, setAssessments] = useState({
    learnerWorkbooks: [
      { 
        id: 1, 
        title: "Workbook 1: React Basics",
        description: "Complete exercises on React components and props",
        type: "Learner Workbook",
        dueDate: "2026-05-15",
        totalMarks: 50,
        status: "published",
        submissions: 18,
        totalLearners: 24
      },
      { 
        id: 2, 
        title: "Workbook 2: State Management",
        description: "Practice useState and useEffect hooks",
        type: "Learner Workbook",
        dueDate: "2026-05-22",
        totalMarks: 60,
        status: "draft",
        submissions: 0,
        totalLearners: 24
      }
    ],
    summative: [
      { 
        id: 3, 
        title: "Mid-term Examination",
        description: "Comprehensive test covering modules 1-4",
        type: "Summative",
        dueDate: "2026-06-10",
        totalMarks: 100,
        status: "published",
        submissions: 12,
        totalLearners: 24
      },
      { 
        id: 4, 
        title: "Final Project",
        description: "Build a complete React application",
        type: "Summative",
        dueDate: "2026-06-30",
        totalMarks: 150,
        status: "published",
        submissions: 5,
        totalLearners: 24
      }
    ]
  });

  const [showModal, setShowModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [selectedGroup, setSelectedGroup] = useState('learnerWorkbooks');
  const [searchTerm, setSearchTerm] = useState('');
  const [openAccordion, setOpenAccordion] = useState({ 0: true, 1: true });
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    dueDate: '',
    totalMarks: '',
    status: 'draft'
  });

  const toggleAccordion = (key) => {
    setOpenAccordion(prev => ({ ...prev, [key]: !prev[key] }));
  };

  const getStatusBadge = (status) => {
    if (status === 'published') {
      return (
        <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-md text-xs font-semibold bg-green-100 text-green-700">
          LIVE
        </span>
      );
    }
    return (
      <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-md text-xs font-semibold bg-orange-100 text-orange-700">
        DRAFT
      </span>
    );
  };

  const handleOpenModal = (group, item = null) => {
    setSelectedGroup(group);
    if (item) {
      setEditingItem(item);
      setFormData({
        title: item.title,
        description: item.description,
        dueDate: item.dueDate,
        totalMarks: item.totalMarks,
        status: item.status
      });
    } else {
      setEditingItem(null);
      setFormData({
        title: '',
        description: '',
        dueDate: '',
        totalMarks: '',
        status: 'draft'
      });
    }
    setShowModal(true);
  };

  const handleSave = () => {
    if (!formData.title) {
      alert('Please fill in required fields');
      return;
    }

    const newItem = {
      id: editingItem ? editingItem.id : Date.now(),
      ...formData,
      type: selectedGroup === 'learnerWorkbooks' ? 'Learner Workbook' : 'Summative',
      totalMarks: parseInt(formData.totalMarks),
      submissions: editingItem ? editingItem.submissions : 0,
      totalLearners: 24
    };

    if (editingItem) {
      setAssessments(prev => ({
        ...prev,
        [selectedGroup]: prev[selectedGroup].map(item =>
          item.id === editingItem.id ? newItem : item
        )
      }));
    } else {
      setAssessments(prev => ({
        ...prev,
        [selectedGroup]: [...prev[selectedGroup], newItem]
      }));
    }
    setShowModal(false);
  };

  const handleDelete = () => {
    setAssessments(prev => ({
      ...prev,
      [selectedGroup]: prev[selectedGroup].filter(item => item.id !== editingItem?.id)
    }));
    setShowDeleteModal(false);
    setEditingItem(null);
  };

  const getFilteredItems = (items) => {
    return items.filter(item =>
      item.title.toLowerCase().includes(searchTerm.toLowerCase())
    );
  };

  const getProgressColor = (submissions, total) => {
    const pct = (submissions / total) * 100;
    if (pct >= 75) return 'bg-green-500';
    if (pct >= 50) return 'bg-yellow-500';
    return 'bg-red-500';
  };

  return (
    <div className="min-h-screen bg-gray-50 text-gray-900">
      <div className="max-w-5xl mx-auto px-6 py-8">

        {/* Header */}
        <div className="mb-8">
          <div className="flex flex-col md:flex-row md:justify-between md:items-end gap-4">
            <div>
              <div className="flex items-center gap-3 mb-2">
                <div className="w-10 h-10 rounded-lg bg-blue-600 flex items-center justify-center">
                  <FaLayerGroup className="text-white text-lg" />
                </div>
                <h1 className="text-3xl font-bold text-gray-900">
                  Assessments
                </h1>
              </div>
              <p className="text-gray-500 text-sm ml-1">
                Manage learner workbooks and summative assessments
              </p>
            </div>
          </div>
        </div>

        {/* Search Bar */}
        <div className="mb-6">
          <div className="relative">
            <FaSearch className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              placeholder="Search assessments..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full bg-white border border-gray-200 rounded-xl pl-11 pr-4 py-3 text-gray-900 placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 text-sm"
            />
            {searchTerm && (
              <button 
                onClick={() => setSearchTerm('')}
                className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
              >
                <FaTimes size={14} />
              </button>
            )}
          </div>
        </div>

        {/* Accordion Groups */}
        <div className="space-y-4">
          {/* Learner Workbooks */}
          <div className="rounded-xl border border-gray-200 bg-white overflow-hidden">
            <button 
              onClick={() => toggleAccordion(0)}
              className="w-full flex items-center justify-between p-5 hover:bg-gray-50 transition-colors"
            >
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-lg bg-blue-100 flex items-center justify-center">
                  <FaBook className="text-blue-600" />
                </div>
                <div className="text-left">
                  <h2 className="text-lg font-semibold text-gray-900">
                    Learner Workbooks
                  </h2>
                </div>
                <span className="ml-3 px-3 py-1 rounded-full bg-blue-50 text-blue-700 text-sm font-semibold">
                  {assessments.learnerWorkbooks.length}
                </span>
              </div>
              <div className="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center">
                {openAccordion[0] ? <FaChevronUp className="text-gray-500" size={14} /> : <FaChevronDown className="text-gray-500" size={14} />}
              </div>
            </button>

            {openAccordion[0] && (
              <div className="px-5 pb-5">
                <div className="space-y-2">
                  {getFilteredItems(assessments.learnerWorkbooks).length === 0 ? (
                    <div className="p-10 text-center rounded-xl border-2 border-dashed border-gray-200">
                      <FaFileAlt className="text-gray-300 text-4xl mx-auto mb-3" />
                      <p className="text-gray-500 mb-4">No learner workbooks found</p>
                      <button 
                        onClick={() => handleOpenModal('learnerWorkbooks')}
                        className="px-5 py-2 rounded-lg bg-blue-600 text-white font-medium text-sm hover:bg-blue-700 transition"
                      >
                        <FaPlus className="inline mr-2" size={12} /> Add Workbook
                      </button>
                    </div>
                  ) : (
                    getFilteredItems(assessments.learnerWorkbooks).map((item) => (
                      <div 
                        key={item.id} 
                        className="rounded-lg border border-gray-100 bg-gray-50 p-4 hover:border-blue-200 transition-colors"
                      >
                        <div className="flex items-start justify-between gap-4">
                          <div className="flex-1 min-w-0">
                            <div className="flex items-center gap-2 mb-1 flex-wrap">
                              <h4 className="font-semibold text-gray-900">{item.title}</h4>
                              {getStatusBadge(item.status)}
                            </div>
                            <p className="text-gray-500 text-sm mb-3">{item.description}</p>

                            <div className="flex flex-wrap items-center gap-4 text-xs text-gray-500">
                              <span className="flex items-center gap-1.5">
                                <FaCalendarAlt size={12} />
                                {new Date(item.dueDate).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}
                              </span>
                              <span className="flex items-center gap-1.5">
                                <FaStar size={12} />
                                {item.totalMarks} pts
                              </span>
                              <span className="flex items-center gap-1.5">
                                <FaCheckCircle size={12} />
                                {item.submissions}/{item.totalLearners}
                              </span>
                            </div>

                            {/* Progress Bar */}
                            <div className="mt-3 flex items-center gap-3">
                              <div className="flex-1 h-1.5 rounded-full bg-gray-200 overflow-hidden">
                                <div 
                                  className={`h-full rounded-full ${getProgressColor(item.submissions, item.totalLearners)}`}
                                  style={{ width: `${(item.submissions / item.totalLearners) * 100}%` }}
                                />
                              </div>
                              <span className="text-xs text-gray-400 font-medium">
                                {Math.round((item.submissions / item.totalLearners) * 100)}%
                              </span>
                            </div>
                          </div>

                          <div className="flex flex-col gap-2">
                            <button 
                              onClick={() => handleOpenModal('learnerWorkbooks', item)}
                              className="w-8 h-8 rounded-lg bg-white border border-gray-200 hover:bg-blue-50 hover:border-blue-300 text-gray-500 hover:text-blue-600 flex items-center justify-center transition"
                              title="Edit"
                            >
                              <FaEdit size={13} />
                            </button>
                            <button 
                              onClick={() => {
                                setEditingItem(item);
                                setSelectedGroup('learnerWorkbooks');
                                setShowDeleteModal(true);
                              }}
                              className="w-8 h-8 rounded-lg bg-white border border-gray-200 hover:bg-red-50 hover:border-red-300 text-gray-500 hover:text-red-600 flex items-center justify-center transition"
                              title="Delete"
                            >
                              <FaTrash size={13} />
                            </button>
                          </div>
                        </div>
                      </div>
                    ))
                  )}
                </div>
              </div>
            )}
          </div>

          {/* Summative Assessments */}
          <div className="rounded-xl border border-gray-200 bg-white overflow-hidden">
            <button 
              onClick={() => toggleAccordion(1)}
              className="w-full flex items-center justify-between p-5 hover:bg-gray-50 transition-colors"
            >
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-lg bg-purple-100 flex items-center justify-center">
                  <FaClipboardList className="text-purple-600" />
                </div>
                <div className="text-left">
                  <h2 className="text-lg font-semibold text-gray-900">
                    Summative Assessments
                  </h2>
                </div>
                <span className="ml-3 px-3 py-1 rounded-full bg-purple-50 text-purple-700 text-sm font-semibold">
                  {assessments.summative.length}
                </span>
              </div>
              <div className="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center">
                {openAccordion[1] ? <FaChevronUp className="text-gray-500" size={14} /> : <FaChevronDown className="text-gray-500" size={14} />}
              </div>
            </button>

            {openAccordion[1] && (
              <div className="px-5 pb-5">
                <div className="space-y-2">
                  {getFilteredItems(assessments.summative).length === 0 ? (
                    <div className="p-10 text-center rounded-xl border-2 border-dashed border-gray-200">
                      <FaClipboardList className="text-gray-300 text-4xl mx-auto mb-3" />
                      <p className="text-gray-500 mb-4">No summative assessments found</p>
                      <button 
                        onClick={() => handleOpenModal('summative')}
                        className="px-5 py-2 rounded-lg bg-purple-600 text-white font-medium text-sm hover:bg-purple-700 transition"
                      >
                        <FaPlus className="inline mr-2" size={12} /> Add Assessment
                      </button>
                    </div>
                  ) : (
                    getFilteredItems(assessments.summative).map((item) => (
                      <div 
                        key={item.id} 
                        className="rounded-lg border border-gray-100 bg-gray-50 p-4 hover:border-purple-200 transition-colors"
                      >
                        <div className="flex items-start justify-between gap-4">
                          <div className="flex-1 min-w-0">
                            <div className="flex items-center gap-2 mb-1 flex-wrap">
                              <h4 className="font-semibold text-gray-900">{item.title}</h4>
                              {getStatusBadge(item.status)}
                            </div>
                            <p className="text-gray-500 text-sm mb-3">{item.description}</p>

                            <div className="flex flex-wrap items-center gap-4 text-xs text-gray-500">
                              <span className="flex items-center gap-1.5">
                                <FaCalendarAlt size={12} />
                                {new Date(item.dueDate).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}
                              </span>
                              <span className="flex items-center gap-1.5">
                                <FaStar size={12} />
                                {item.totalMarks} pts
                              </span>
                              <span className="flex items-center gap-1.5">
                                <FaCheckCircle size={12} />
                                {item.submissions}/{item.totalLearners}
                              </span>
                            </div>

                            {/* Progress Bar */}
                            <div className="mt-3 flex items-center gap-3">
                              <div className="flex-1 h-1.5 rounded-full bg-gray-200 overflow-hidden">
                                <div 
                                  className={`h-full rounded-full ${getProgressColor(item.submissions, item.totalLearners)}`}
                                  style={{ width: `${(item.submissions / item.totalLearners) * 100}%` }}
                                />
                              </div>
                              <span className="text-xs text-gray-400 font-medium">
                                {Math.round((item.submissions / item.totalLearners) * 100)}%
                              </span>
                            </div>
                          </div>

                          <div className="flex flex-col gap-2">
                            <button 
                              onClick={() => handleOpenModal('summative', item)}
                              className="w-8 h-8 rounded-lg bg-white border border-gray-200 hover:bg-purple-50 hover:border-purple-300 text-gray-500 hover:text-purple-600 flex items-center justify-center transition"
                              title="Edit"
                            >
                              <FaEdit size={13} />
                            </button>
                            <button 
                              onClick={() => {
                                setEditingItem(item);
                                setSelectedGroup('summative');
                                setShowDeleteModal(true);
                              }}
                              className="w-8 h-8 rounded-lg bg-white border border-gray-200 hover:bg-red-50 hover:border-red-300 text-gray-500 hover:text-red-600 flex items-center justify-center transition"
                              title="Delete"
                            >
                              <FaTrash size={13} />
                            </button>
                          </div>
                        </div>
                      </div>
                    ))
                  )}
                </div>
              </div>
            )}
          </div>
        </div>

        {/* Floating Action Button */}
        <div className="fixed bottom-8 right-8">
          <button 
            onClick={() => handleOpenModal('learnerWorkbooks')}
            className="w-14 h-14 rounded-full bg-blue-600 text-white flex items-center justify-center shadow-lg hover:bg-blue-700 transition"
          >
            <FaPlus size={20} />
          </button>
        </div>
      </div>

      {/* Add/Edit Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
          <div className="absolute inset-0 bg-black/40" onClick={() => setShowModal(false)} />
          <div className="relative w-full max-w-lg rounded-xl border border-gray-200 bg-white shadow-xl overflow-hidden">
            {/* Modal Header */}
            <div className="px-6 py-4 border-b border-gray-100 flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className={`w-9 h-9 rounded-lg flex items-center justify-center ${selectedGroup === 'learnerWorkbooks' ? 'bg-blue-100' : 'bg-purple-100'}`}>
                  {editingItem ? <FaEdit className={selectedGroup === 'learnerWorkbooks' ? 'text-blue-600' : 'text-purple-600'} /> : <FaPlus className={selectedGroup === 'learnerWorkbooks' ? 'text-blue-600' : 'text-purple-600'} />}
                </div>
                <h3 className="text-lg font-semibold text-gray-900">
                  {editingItem ? 'Edit Assessment' : `Add ${selectedGroup === 'learnerWorkbooks' ? 'Learner Workbook' : 'Summative Assessment'}`}
                </h3>
              </div>
              <button 
                onClick={() => setShowModal(false)}
                className="w-8 h-8 rounded-full bg-gray-100 hover:bg-gray-200 text-gray-500 flex items-center justify-center transition"
              >
                <FaTimes size={14} />
              </button>
            </div>

            {/* Modal Body */}
            <div className="px-6 py-5 space-y-4">
              <div>
                <label className="block text-xs font-semibold text-gray-700 uppercase tracking-wide mb-1.5">Title *</label>
                <input
                  type="text"
                  placeholder="Enter assessment title"
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  className="w-full bg-white border border-gray-200 rounded-lg px-4 py-2.5 text-gray-900 placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 text-sm"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-700 uppercase tracking-wide mb-1.5">Description</label>
                <textarea
                  rows={3}
                  placeholder="Enter description"
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  className="w-full bg-white border border-gray-200 rounded-lg px-4 py-2.5 text-gray-900 placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 text-sm resize-none"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-gray-700 uppercase tracking-wide mb-1.5">Due Date</label>
                  <input
                    type="date"
                    value={formData.dueDate}
                    onChange={(e) => setFormData({ ...formData, dueDate: e.target.value })}
                    className="w-full bg-white border border-gray-200 rounded-lg px-4 py-2.5 text-gray-900 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 text-sm"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-gray-700 uppercase tracking-wide mb-1.5">Total Marks</label>
                  <input
                    type="number"
                    placeholder="e.g., 100"
                    value={formData.totalMarks}
                    onChange={(e) => setFormData({ ...formData, totalMarks: e.target.value })}
                    className="w-full bg-white border border-gray-200 rounded-lg px-4 py-2.5 text-gray-900 placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 text-sm"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-700 uppercase tracking-wide mb-1.5">Status</label>
                <div className="grid grid-cols-2 gap-3">
                  <button
                    onClick={() => setFormData({ ...formData, status: 'draft' })}
                    className={`px-4 py-2.5 rounded-lg border text-sm font-medium transition ${formData.status === 'draft' ? 'bg-orange-50 border-orange-300 text-orange-700' : 'bg-white border-gray-200 text-gray-600 hover:border-gray-300'}`}
                  >
                    <FaHourglassHalf className="inline mr-2" size={12} />
                    Draft
                  </button>
                  <button
                    onClick={() => setFormData({ ...formData, status: 'published' })}
                    className={`px-4 py-2.5 rounded-lg border text-sm font-medium transition ${formData.status === 'published' ? 'bg-green-50 border-green-300 text-green-700' : 'bg-white border-gray-200 text-gray-600 hover:border-gray-300'}`}
                  >
                    <FaBolt className="inline mr-2" size={12} />
                    Published
                  </button>
                </div>
              </div>
            </div>

            {/* Modal Footer */}
            <div className="px-6 py-4 border-t border-gray-100 flex justify-end gap-3">
              <button 
                onClick={() => setShowModal(false)}
                className="px-5 py-2 rounded-lg bg-gray-100 text-gray-700 font-medium text-sm hover:bg-gray-200 transition"
              >
                Cancel
              </button>
              <button 
                onClick={handleSave}
                className="px-5 py-2 rounded-lg bg-blue-600 text-white font-medium text-sm hover:bg-blue-700 transition"
              >
                Save Assessment
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Delete Confirmation Modal */}
      {showDeleteModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
          <div className="absolute inset-0 bg-black/40" onClick={() => setShowDeleteModal(false)} />
          <div className="relative w-full max-w-sm rounded-xl border border-gray-200 bg-white shadow-xl overflow-hidden">
            <div className="px-6 py-6 text-center">
              <div className="w-14 h-14 rounded-full bg-red-100 flex items-center justify-center mx-auto mb-4">
                <FaTrash className="text-red-600 text-lg" />
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-1">Confirm Delete</h3>
              <p className="text-gray-500 text-sm mb-1">Are you sure you want to delete</p>
              <p className="text-gray-900 font-semibold mb-4">{editingItem?.title}</p>
              <p className="text-xs text-gray-400 mb-6">This action cannot be undone.</p>

              <div className="flex gap-3 justify-center">
                <button 
                  onClick={() => setShowDeleteModal(false)}
                  className="px-5 py-2 rounded-lg bg-gray-100 text-gray-700 font-medium text-sm hover:bg-gray-200 transition"
                >
                  Cancel
                </button>
                <button 
                  onClick={handleDelete}
                  className="px-5 py-2 rounded-lg bg-red-600 text-white font-medium text-sm hover:bg-red-700 transition"
                >
                  Delete
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
